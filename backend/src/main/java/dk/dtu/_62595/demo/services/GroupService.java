package dk.dtu._62595.demo.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dk.dtu._62595.demo.model.Group;
import dk.dtu._62595.demo.model.GroupMember;
import dk.dtu._62595.demo.model.User;
import dk.dtu._62595.demo.repositories.GroupMemberRepository;
import dk.dtu._62595.demo.repositories.GroupRepository;
import dk.dtu._62595.demo.repositories.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class GroupService {
	@Autowired
	GroupRepository groupRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	GroupMemberRepository groupMemberRepository;

	@Transactional
	public Group createGroup(String name, User currentUser) {
		Group group = new Group(name);
		Group saved = groupRepository.save(group);
		GroupMember member = new GroupMember(currentUser, saved, GroupMember.Role.OWNER);
		groupMemberRepository.save(member);
		return saved;
	}

	@Transactional
	public Group updateGroup(UUID groupId, String name) {
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
		group.setName(name);
		return groupRepository.save(group);
	}

	@Transactional
	public void deleteGroup(UUID groupId) {
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
		groupRepository.deleteById(group.getId());
	}

	@Transactional
	public Group getGroupById(UUID groupId) {
		return groupRepository.findById(groupId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
	}

	@Transactional
	public List<Group> getGroupsForUser(User currentUser) {
		return groupMemberRepository.findByUser(currentUser)
				.stream()
				.map(GroupMember::getGroup)
				.toList();
	}

	@Transactional
	public GroupMember.Role getRole(Group group, User user) {
		return groupMemberRepository.findById(new GroupMember.GroupMemberId(user.getId(), group.getId())).orElseThrow().getRole();
	}

	@Transactional
	public boolean canUserEditGroup(Group group, User user) {
		try {
			return this.getRole(group, user) != GroupMember.Role.MEMBER;
		} catch (NoSuchElementException exception) {
			return false;
		}
	}

	public boolean canUserViewGroup(Group group, User user) {
		Optional<GroupMember> member = groupMemberRepository.findById(new GroupMember.GroupMemberId(user.getId(), group.getId()));
		return member.isPresent();
	}

	@Transactional
	public List<GroupMember> getGroupMembers(Group group) {
		return groupMemberRepository.findByGroup(group);
	}

	@Transactional
	public void addGroupMember(Group group, User member, GroupMember.Role role) {
		groupMemberRepository.save(new GroupMember(member, group, role));
	}

	@Transactional
	public void editGroupMember(GroupMember groupMember) {
		groupMemberRepository.save(groupMember);
	}

	@Transactional
	public void removeGroupMember(Group group, User member) {
		groupMemberRepository.delete(groupMemberRepository.findById(new GroupMember.GroupMemberId(member.getId(), group.getId())).orElseThrow());
	}
	@Transactional
	public void cleanupUserGroups(User user) {
		// Get all memberships for this user
		List<GroupMember> memberships = groupMemberRepository.findByUser(user);

		for (GroupMember membership : memberships) {
			Group group = membership.getGroup();

			// If the user is the owner, we need a successor
			if (membership.getRole() == GroupMember.Role.OWNER) {
				transferOwnershipOrDelete(group, user);
			}

			// Remove the user from the group (maybe not necessary as it should cascade from user deletion.
			groupMemberRepository.delete(membership);
		}
	}

	private void transferOwnershipOrDelete(Group group, User userBeingDeleted) {
		List<GroupMember> otherMembers = groupMemberRepository.findByGroup(group).stream()
				.filter(m -> !m.getUser().getId().equals(userBeingDeleted.getId()))
				.toList();

		if (otherMembers.isEmpty()) {
			// No one left to take over => wipe the group
			groupRepository.deleteById(group.getId());
		} else {
			// Find the best successor: Prefer ADMIN, else first available
			GroupMember successor = otherMembers.stream()
					.filter(m -> m.getRole() == GroupMember.Role.ADMIN)
					.findFirst()
					.orElse(otherMembers.getFirst());

			successor.setRole(GroupMember.Role.OWNER);
			groupMemberRepository.save(successor);
		}
	}
}