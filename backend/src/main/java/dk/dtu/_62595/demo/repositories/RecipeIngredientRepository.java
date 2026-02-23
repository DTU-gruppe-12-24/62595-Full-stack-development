package dk.dtu._62595.demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;

import dk.dtu._62595.demo.model.Recipe;
import dk.dtu._62595.demo.model.RecipeIngredient;
import dk.dtu._62595.demo.model.RecipeIngredient.RecipeIngredientId;

public interface RecipeIngredientRepository extends Repository<RecipeIngredient, RecipeIngredientId> {
    // Persistence operations
    RecipeIngredient save(RecipeIngredient entity);
    void delete(RecipeIngredient entity);

    // Retrieval operations
    Optional<RecipeIngredient> findById(RecipeIngredientId id);
    List<RecipeIngredient> findAll();

    // Derived queries
    List<RecipeIngredient> findByRecipe(Recipe recipe);
}