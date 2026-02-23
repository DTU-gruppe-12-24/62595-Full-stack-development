package dk.dtu._62595.demo.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

@Service
public class GroupService {
	@Autowired
	GroupRepository groupRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	GroupMemberRepository groupMemberRepository;

	public Group createGroup(String name, User currentUser) {
		Group group = new Group(name);
		Group saved = groupRepository.save(group);
		GroupMember member = new GroupMember(currentUser, saved, "owner");
		groupMemberRepository.save(member);
		return saved;
	}

	public Group updateGroup(UUID groupId, String name) {
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
		group.setName(name);
		return groupRepository.save(group);
	}

	public void deleteGroup(UUID groupId) {
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
		groupMemberRepository.findByGroup(group)
				.forEach(groupMemberRepository::delete);
		groupRepository.deleteById(groupId);
	}

	public Group getGroupById(UUID groupId) {
		return groupRepository.findById(groupId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
	}

	public List<Group> getGroupsForUser(User currentUser) {
		return groupMemberRepository.findByUser(currentUser)
				.stream()
				.map(GroupMember::getGroup)
				.collect(Collectors.toList());
	}
}