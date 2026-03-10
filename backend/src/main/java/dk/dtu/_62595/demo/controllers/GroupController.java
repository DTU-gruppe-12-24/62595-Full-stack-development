package dk.dtu._62595.demo.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dk.dtu._62595.demo.model.Group;
import dk.dtu._62595.demo.services.GroupService;

@RestController
@RequestMapping(value = "/api/group", consumes = { "application/xml", "application/json" })
public class GroupController {
	@Autowired
	AuthController authController;

	@Autowired
	GroupService groupService;

	@PostMapping
	public Group create(@RequestBody Group group) {
		return groupService.createGroup(group.getName(), authController.getLoggedInUser());
	}

	@PutMapping("/{groupId}")
	public Group update(@PathVariable UUID groupId, @RequestBody Group group) {
		return groupService.updateGroup(groupId, group.getName());
	}

	@DeleteMapping("/{groupId}")
	public void delete(@PathVariable UUID groupId) {
		groupService.deleteGroup(groupId);
	}

	@GetMapping("/{groupId}")
	public Group getById(@PathVariable UUID groupId) {
		return groupService.getGroupById(groupId);
	}

	@GetMapping("/me")
	public List<Group> getMyGroups() {
		return groupService.getGroupsForUser(authController.getLoggedInUser());
	}
}