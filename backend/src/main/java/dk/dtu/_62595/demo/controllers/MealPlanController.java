package dk.dtu._62595.demo.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import dk.dtu._62595.demo.dto.CreateMealPlanRequest;
import dk.dtu._62595.demo.dto.MealPlanResponse;
import dk.dtu._62595.demo.services.MealPlanService;
import dk.dtu._62595.demo.dto.UpdateMealPlanRequest;

@RestController
@RequestMapping("/api/meal-plans")
@CrossOrigin
public class MealPlanController {

    private final MealPlanService mealPlanService;

    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MealPlanResponse create(@RequestBody CreateMealPlanRequest request) {
        return mealPlanService.create(request);
    }

    @GetMapping
    public List<MealPlanResponse> getByDate(
            @RequestParam UUID groupId,
            @RequestParam LocalDate date
    ) {
        return mealPlanService.getByDate(groupId, date);
    }

    @GetMapping("/range")
    public List<MealPlanResponse> getByRange(
            @RequestParam UUID groupId,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    ) {
        return mealPlanService.getByRange(groupId, start, end);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        mealPlanService.delete(id);
    }

    @PutMapping("/{id}")
    public MealPlanResponse update(
            @PathVariable UUID id,
            @RequestBody UpdateMealPlanRequest request
    ) {
        return mealPlanService.update(id, request);
    }
}


