package dk.dtu._62595.demo.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dk.dtu._62595.demo.model.Group;
import dk.dtu._62595.demo.model.User;
import dk.dtu._62595.demo.services.GroupService;

@RestController
@RequestMapping("/api/group")
public class GroupController {
	@Autowired
	GroupService groupService;

	@PostMapping
	public Group create(@RequestParam String name, @RequestAttribute("currentUser") User currentUser) {
		return groupService.createGroup(name, currentUser);
	}

	@PutMapping("/{groupId}")
	public Group update(@PathVariable UUID groupId, @RequestParam String name) {
		return groupService.updateGroup(groupId, name);
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
	public List<Group> getMyGroups(@RequestAttribute("currentUser") User currentUser) {
		return groupService.getGroupsForUser(currentUser);
	}
}