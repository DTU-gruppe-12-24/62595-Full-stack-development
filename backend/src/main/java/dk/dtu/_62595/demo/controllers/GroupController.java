package dk.dtu._62595.demo.controllers;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import dk.dtu._62595.demo.dto.MyGroupResponse;
import dk.dtu._62595.demo.model.Group;
import dk.dtu._62595.demo.model.GroupMember;
import dk.dtu._62595.demo.model.User;
import dk.dtu._62595.demo.services.GroupService;
import dk.dtu._62595.demo.services.UserService;

@RestController
@RequestMapping(value = "/api/group", consumes = { "application/xml", "application/json" })
public class GroupController {
	@Autowired
	AuthController authController;
	@Autowired
	GroupService groupService;
	@Autowired
	UserService userService;

	@PostMapping
	public Group create(@RequestBody Group group) {
		return groupService.createGroup(group.getName(), authController.getLoggedInUser());
	}

	@PutMapping("/{groupId}")
	public Group update(@PathVariable UUID groupId, @RequestBody Group group) {
		Group currentGroup = groupService.getGroupById(groupId);
		User currentUser = authController.getLoggedInUser();
		if (!groupService.canUserEditGroup(currentGroup, currentUser)) throw new AuthorizationDeniedException("You do not have permission to edit this group!");
		return groupService.updateGroup(groupId, group.getName());
	}

	@DeleteMapping("/{groupId}")
	public void delete(@PathVariable UUID groupId) {
		Group currentGroup = groupService.getGroupById(groupId);
		User currentUser = authController.getLoggedInUser();
		if (!groupService.canUserEditGroup(currentGroup, currentUser)) throw new AuthorizationDeniedException("You do not have permission to delete this group!");
		groupService.deleteGroup(groupId);
	}

	@GetMapping("/{groupId}")
	public Group getById(@PathVariable UUID groupId) {
		Group group = groupService.getGroupById(groupId);
		User currentUser = authController.getLoggedInUser();
		if (!groupService.canUserViewGroup(group, currentUser)) throw new AuthorizationDeniedException("You do not have permission to view this group!");
		return group;
	}

	@GetMapping("/me")
	public List<MyGroupResponse> getMyGroups() {
		User user = authController.getLoggedInUser();
		return groupService.getGroupsForUser(user)
			.stream()
			.map(group -> new MyGroupResponse(group, groupService.getRole(group, user)))
			.toList();
	}

	@GetMapping("/{groupId}/members")
	public List<GroupMember> getMembersById(@PathVariable UUID groupId) {
		Group group = groupService.getGroupById(groupId);
		User currentUser = authController.getLoggedInUser();
		if (!groupService.canUserViewGroup(group, currentUser)) throw new AuthorizationDeniedException("You do not have permission to view this group!");
		return groupService.getGroupMembers(group);
	}

	@PostMapping("/{groupId}/members")
	public List<GroupMember> updateMembersById(@PathVariable UUID groupId, @RequestBody List<GroupMember> members) {
		Group group = groupService.getGroupById(groupId);
		User currentUser = authController.getLoggedInUser();
		GroupMember.Role myRole;
		try {
			myRole = groupService.getRole(group, currentUser);
			if (myRole == GroupMember.Role.MEMBER) throw new AuthorizationDeniedException("");
		} catch (Exception exception) {
			throw new AuthorizationDeniedException("You do not have permission to edit this group!");
		}

		members.forEach(member -> {
			if (member.getUser() == null || member.getRole() == null)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Each member must have a user and a role");
			try {
				GroupMember.Role role = groupService.getRole(group, member.getUser());
				if (role == GroupMember.Role.OWNER) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't change the group owner's role");
				if (role == GroupMember.Role.ADMIN && myRole != GroupMember.Role.OWNER) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only the owner can edit group admins");
			} catch (NoSuchElementException exception) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Each member must already be in group");
			}
			groupService.editGroupMember(new GroupMember(member.getUser(), group, member.getRole()));
		});

		return groupService.getGroupMembers(group);
	}

	@PostMapping("/{groupId}/invite/{email}")
	public boolean inviteMember(@PathVariable UUID groupId, @PathVariable String email, @RequestParam(required = false) GroupMember.Role role) {
		User currentUser = authController.getLoggedInUser();
		Group group = groupService.getGroupById(groupId);
		if (!groupService.canUserEditGroup(group, currentUser)) throw new AuthorizationDeniedException("You do not have permission to edit this group!");

		User invitedUser = userService.find(email).orElse(null);
		if (invitedUser == null) return false; // User with the given email does not exist

		if (groupService.canUserViewGroup(group, invitedUser)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already a member of the group.");

		groupService.addGroupMember(group, invitedUser, role == null ? GroupMember.Role.MEMBER : role);
		return true;
	}

	@DeleteMapping("/{groupId}/members/{userId}")
	public void removeMember(@PathVariable UUID groupId, @PathVariable UUID userId) {
		User currentUser = authController.getLoggedInUser();
		Group group = groupService.getGroupById(groupId);
		User userToRemove = userService.find(userId).orElseThrow();

		GroupMember.Role ownRole = groupService.getRole(group, currentUser);
		GroupMember.Role removeRole = groupService.getRole(group, userToRemove);

		if (removeRole == GroupMember.Role.OWNER) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Can't remove owner of repository.");

		if (!(
			currentUser.getId() == userToRemove.getId() ||
			removeRole == GroupMember.Role.MEMBER ||
			ownRole == GroupMember.Role.OWNER
		)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to remove this member.");

		groupService.removeGroupMember(group, userToRemove);
	}
}