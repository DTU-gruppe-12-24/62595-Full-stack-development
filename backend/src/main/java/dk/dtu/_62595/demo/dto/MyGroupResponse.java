package dk.dtu._62595.demo.dto;

import dk.dtu._62595.demo.model.Group;
import dk.dtu._62595.demo.model.GroupMember.Role;

public record MyGroupResponse(Group group, Role role) {};
