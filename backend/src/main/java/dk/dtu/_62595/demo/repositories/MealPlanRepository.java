package dk.dtu._62595.demo.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.Repository;

import dk.dtu._62595.demo.model.Group;
import dk.dtu._62595.demo.model.MealPlan;
import dk.dtu._62595.demo.model.Recipe;

public interface MealPlanRepository extends Repository<MealPlan, UUID> {
    // Persistence operations
    MealPlan save(MealPlan mealPlan);
    void deleteById(UUID id);

    // Retrieval operations
    Optional<MealPlan> findById(UUID id);
    List<MealPlan> findAll();

    // Derived queries
    List<MealPlan> findByGroup(Group group);
    List<MealPlan> findByGroupAndRecipe(Group group, Recipe recipe);
    List<MealPlan> findByGroupAndScheduledDateBetween(Group group, LocalDate start, LocalDate end);
    List<MealPlan> findByGroupAndMealSlot(Group group, String slot);
}