package dk.dtu._62595.demo.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import dk.dtu._62595.demo.model.Group;

public interface GroupRepository extends JpaRepository<Group, UUID> {
	// Persistence operations
	Group save(Group group);
	void deleteById(UUID id);

	// Retrieval operations
	Optional<Group> findById(UUID id);
	List<Group> findAll();

	// Derived queries
	Optional<Group> findByName(String name);
}