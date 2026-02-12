package dk.dtu._62595.demo.repositories;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import dk.dtu._62595.demo.model.Test;

public interface TestRepository extends Repository<Test, Long> {
	Optional<Test> findById(Long id);
	Optional<Test> findByName(String name);
}
