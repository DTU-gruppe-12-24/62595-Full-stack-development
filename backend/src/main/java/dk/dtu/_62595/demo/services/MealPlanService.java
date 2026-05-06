package dk.dtu._62595.demo.services;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import dk.dtu._62595.demo.model.User;
import dk.dtu._62595.demo.repositories.UserRepository;
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
    private final UserRepository userRepository;

    public MealPlanService(
            MealPlanRepository mealPlanRepository,
            GroupRepository groupRepository,
            RecipeRepository recipeRepository,
            UserRepository userRepository
    ) {
        this.mealPlanRepository = mealPlanRepository;
        this.groupRepository = groupRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
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
        if (request.cookerId != null) {
            User cooker = userRepository.findById(request.cookerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cooker user not found"));
            mealPlan.setCooker(cooker);
        }

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
                mealPlan.getMealSlot(),
                mealPlan.getCooker() != null ? mealPlan.getCooker().getId() : null,
                mealPlan.getCooker() != null ? mealPlan.getCooker().getName() : null        );
    }

    public MealPlanResponse update(UUID id, UpdateMealPlanRequest request) {
        MealPlan mealPlan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meal plan not found"));

        mealPlan.setScheduledDate(request.scheduledDate);
        mealPlan.setMealSlot(request.mealSlot);

        MealPlan saved = mealPlanRepository.save(mealPlan);

        return toResponse(saved);
    }

    public MealPlanResponse updateCooker(UUID mealPlanId, UUID userId) {
        MealPlan mealPlan = mealPlanRepository.findById(mealPlanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meal plan not found"));

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            mealPlan.setCooker(user);
        } else {
            mealPlan.setCooker(null);
        }

        return toResponse(mealPlanRepository.save(mealPlan));
    }
}
