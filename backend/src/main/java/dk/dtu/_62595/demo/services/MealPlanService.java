package dk.dtu._62595.demo.services;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dk.dtu._62595.demo.dto.CreateMealPlanRequest;
import dk.dtu._62595.demo.dto.MealPlanResponse;
import dk.dtu._62595.demo.model.Group;
import dk.dtu._62595.demo.model.MealPlan;
import dk.dtu._62595.demo.model.Recipe;
import dk.dtu._62595.demo.repositories.GroupRepository;
import dk.dtu._62595.demo.repositories.MealPlanRepository;
import dk.dtu._62595.demo.repositories.RecipeRepository;
import dk.dtu._62595.demo.dto.UpdateMealPlanRequest;

@Service
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final GroupRepository groupRepository;
    private final RecipeRepository recipeRepository;

    public MealPlanService(
            MealPlanRepository mealPlanRepository,
            GroupRepository groupRepository,
            RecipeRepository recipeRepository
    ) {
        this.mealPlanRepository = mealPlanRepository;
        this.groupRepository = groupRepository;
        this.recipeRepository = recipeRepository;
    }

    public MealPlanResponse create(CreateMealPlanRequest request) {
        if (request.groupId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "groupId is required");
        }
        if (request.recipeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "recipeId is required");
        }
        if (request.scheduledDate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "scheduledDate is required");
        }
        if (request.mealSlot == null || request.mealSlot.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mealSlot is required");
        }

        Group group = groupRepository.findById(request.groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));

        Recipe recipe = recipeRepository.findById(request.recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));

        MealPlan mealPlan = new MealPlan(
                group,
                recipe,
                request.scheduledDate,
                request.mealSlot
        );

        MealPlan saved = mealPlanRepository.save(mealPlan);
        return toResponse(saved);
    }

    public List<MealPlanResponse> getByDate(UUID groupId, LocalDate date) {
        if (groupId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "groupId is required");
        }
        if (date == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "date is required");
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));

        return mealPlanRepository.findByGroupAndScheduledDateBetween(group, date, date)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<MealPlanResponse> getByRange(UUID groupId, LocalDate start, LocalDate end) {
        if (groupId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "groupId is required");
        }
        if (start == null || end == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start and end are required");
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));

        return mealPlanRepository.findByGroupAndScheduledDateBetween(group, start, end)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void delete(UUID id) {
        MealPlan mealPlan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meal plan not found"));

        mealPlanRepository.deleteById(mealPlan.getId());
    }

    private MealPlanResponse toResponse(MealPlan mealPlan) {
        return new MealPlanResponse(
                mealPlan.getId(),
                mealPlan.getGroup().getId(),
                mealPlan.getRecipe().getId(),
                mealPlan.getRecipe().getName(),
                mealPlan.getScheduledDate(),
                mealPlan.getMealSlot()
        );
    }

    public MealPlanResponse update(UUID id, UpdateMealPlanRequest request) {
        MealPlan mealPlan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meal plan not found"));

        mealPlan.setScheduledDate(request.scheduledDate);
        mealPlan.setMealSlot(request.mealSlot);

        MealPlan saved = mealPlanRepository.save(mealPlan);

        return toResponse(saved);
    }
}
