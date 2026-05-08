package dk.dtu._62595.demo.services;

import dk.dtu._62595.demo.dto.NutritionStatistics;
import dk.dtu._62595.demo.dto.Statistic;
import dk.dtu._62595.demo.dto.StatisticsSummary;
import dk.dtu._62595.demo.model.Ingredient;
import dk.dtu._62595.demo.model.MealPlan;
import dk.dtu._62595.demo.model.RecipeIngredient;
import dk.dtu._62595.demo.repositories.MealPlanRepository;
import dk.dtu._62595.demo.repositories.RecipeIngredientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class StatisticsService {

    private final MealPlanRepository mealPlanRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;

    public StatisticsService(
            MealPlanRepository mealPlanRepository,
            RecipeIngredientRepository recipeIngredientReposity
    ) {
        this.mealPlanRepository = mealPlanRepository;
        this.recipeIngredientRepository = recipeIngredientReposity;
    }


    public StatisticsSummary getGroupStatistics(UUID groupId, LocalDate start, LocalDate end) {
        List<MealPlan> groupMeals = mealPlanRepository.findByGroup_Id(groupId).stream()
                .filter(mp -> (start == null || !mp.getScheduledDate().isBefore(start)))
                .filter(mp -> (end == null || !mp.getScheduledDate().isAfter(end)))
                .toList();

        long totalMeals = groupMeals.size();
        Map<String, Long> recipeCounts = groupMeals.stream()
                .filter(mp -> mp.getRecipe() != null)
                .collect(Collectors.groupingBy(mp -> mp.getRecipe().getName(), Collectors.counting()));

        Statistic generalStats = new Statistic(totalMeals, recipeCounts);

        NutritionStatistics nutritionStats = calculateTotalNutrition(groupMeals);

        return new StatisticsSummary(generalStats, nutritionStats);


    }

    private NutritionStatistics calculateTotalNutrition(List<MealPlan> meals) {
        double calories = 0.0;
        double protein = 0.0;
        double carbs = 0.0;
        double fat = 0.0;
        double saturatedFat = 0.0;
        double sugars = 0.0;
        double salt = 0.0;

        for (MealPlan meal : meals) {
            if (meal.getRecipe() == null) continue;
            for (RecipeIngredient ri : recipeIngredientRepository.findByRecipe(meal.getRecipe())) {
                if (ri.getIngredient() == null || ri.getUnit() == null) continue;

                float totalGrams = (ri.getAmount() != null ? ri.getAmount() : 0.0f) * ri.getUnit().getGramsPerUnit();
                float multiplier = totalGrams / 100.0f;

                Ingredient ing = ri.getIngredient();
                calories        += (safeGet(ing.getCalories()) * multiplier);
                protein         += (safeGet(ing.getProtein()) * multiplier);
                carbs           += (safeGet(ing.getCarbohydrates()) * multiplier);
                fat             += (safeGet(ing.getFat()) * multiplier);
                saturatedFat    += (safeGet(ing.getSaturatedFat()) * multiplier);
                sugars          += (safeGet(ing.getSugars()) * multiplier);
                salt            += (safeGet(ing.getSalt()) * multiplier);
            }
        }

        return new NutritionStatistics(
                calories,
                protein,
                carbs,
                fat,
                saturatedFat,
                sugars,
                salt
        );
    }
    private double safeGet(Float value) {
        return (value == null) ? 0.0 : value.doubleValue();
    }

}