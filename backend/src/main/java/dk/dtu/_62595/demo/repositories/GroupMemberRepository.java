package dk.dtu._62595.demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import dk.dtu._62595.demo.model.Group;
import dk.dtu._62595.demo.model.GroupMember;
import dk.dtu._62595.demo.model.GroupMember.GroupMemberId;
import dk.dtu._62595.demo.model.User;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {
    // Persistence operations
    GroupMember save(GroupMember entity);
    void delete(GroupMember entity);

    // Retrieval operations
    Optional<GroupMember> findById(GroupMemberId id);
    List<GroupMember> findAll();

    // Derived queries
    List<GroupMember> findByGroup(Group group);
    List<GroupMember> findByUser(User user);
}