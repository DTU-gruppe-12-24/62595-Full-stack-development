package dk.dtu._62595.demo.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.Repository;

import dk.dtu._62595.demo.model.Group;
import dk.dtu._62595.demo.model.Recipe;
import dk.dtu._62595.demo.model.User;

public interface RecipeRepository extends Repository<Recipe, UUID> {
    // Persistence operations
    Recipe save(Recipe recipe);
    void deleteById(UUID id);

    // Retrieval operations
    Optional<Recipe> findById(UUID id);
    List<Recipe> findAll();

    // Derived queries
    List<Recipe> findByOwner(User owner);
    List<Recipe> findByGroup(Group group);
    List<Recipe> findByOwnerAndNameContainingIgnoreCase(User owner, String fragment);
    List<Recipe> findByGroupAndNameContainingIgnoreCase(Group group, String fragment);
    List<Recipe> findByOwnerAndMealType(User owner, String mealType);
    List<Recipe> findByGroupAndMealType(Group group, String mealType);

    UUID id(UUID id);

    void delete(Recipe recipe);
}