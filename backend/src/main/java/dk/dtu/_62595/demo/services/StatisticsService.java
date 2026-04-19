package dk.dtu._62595.demo.services;

import dk.dtu._62595.demo.dto.NutritionStatistics;
import dk.dtu._62595.demo.dto.Statistic;
import dk.dtu._62595.demo.dto.StatisticsSummary;
import dk.dtu._62595.demo.model.MealPlan;
import dk.dtu._62595.demo.repositories.MealPlanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class StatisticsService {

    private final MealPlanRepository mealPlanRepository;

    public StatisticsService(MealPlanRepository mealPlanRepository) {
        this.mealPlanRepository = mealPlanRepository;
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

        NutritionStatistics nutritionStats = mealPlanRepository.getNutritionStatisticsByGroupId(groupId, start, end);
        if (nutritionStats == null) {
            nutritionStats = new NutritionStatistics(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        }

        return new StatisticsSummary(generalStats, nutritionStats);


    }

}