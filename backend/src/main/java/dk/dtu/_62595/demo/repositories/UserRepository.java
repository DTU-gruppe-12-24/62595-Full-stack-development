package dk.dtu._62595.demo.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import dk.dtu._62595.demo.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    // Persistence operations
    User save(User user);
    void delete(User user);

    // Retrieval operations
    Optional<User> findById(UUID id);
    List<User> findAll();

    // Derived queries
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);
}