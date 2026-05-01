package dk.dtu._62595.demo.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import dk.dtu._62595.demo.dto.NutritionStatistics;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import dk.dtu._62595.demo.model.Group;
import dk.dtu._62595.demo.model.MealPlan;
import dk.dtu._62595.demo.model.Recipe;
import org.springframework.data.repository.query.Param;

public interface MealPlanRepository extends JpaRepository<MealPlan, UUID> {
    // Persistence operations
    MealPlan save(MealPlan mealPlan);

    void deleteById(UUID id);

    // Retrieval operations
    Optional<MealPlan> findById(UUID id);

    List<MealPlan> findAll();

    // Derived queries
    List<MealPlan> findByGroup(Group group);

    List<MealPlan> findByGroup_Id(UUID groupId);

    List<MealPlan> findByGroupAndRecipe(Group group, Recipe recipe);

    List<MealPlan> findByGroupAndScheduledDateBetween(Group group, LocalDate start, LocalDate end);

    List<MealPlan> findByGroupAndMealSlot(Group group, String slot);


    @Query("""
    SELECT new dk.dtu._62595.demo.dto.NutritionStatistics(
        CAST(SUM(i.calories * ri.amount) AS double),
        CAST(SUM(i.protein * ri.amount) AS double),
        CAST(SUM(i.carbohydrates * ri.amount) AS double),
        CAST(SUM(i.fat * ri.amount) AS double),
        CAST(SUM(i.saturatedFat * ri.amount) AS double),
        CAST(SUM(i.sugars * ri.amount) AS double),
        CAST(SUM(i.salt * ri.amount) AS double)
    )
    FROM RecipeIngredient ri
    JOIN ri.recipe r
    JOIN ri.ingredient i
    JOIN MealPlan mp ON mp.recipe = r
    WHERE mp.group.id = :groupId
    AND (:start IS NULL OR mp.scheduledDate >= :start)
    AND (:end IS NULL OR mp.scheduledDate <= :end)
""")
    NutritionStatistics getNutritionStatisticsByGroupId(
            @Param("groupId") UUID groupId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}