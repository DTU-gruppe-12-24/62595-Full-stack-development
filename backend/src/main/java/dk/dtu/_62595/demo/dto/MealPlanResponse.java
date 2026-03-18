package dk.dtu._62595.demo.dto;

import java.time.LocalDate;
import java.util.UUID;

public class MealPlanResponse {

    public UUID id;
    public UUID groupId;
    public UUID recipeId;
    public String recipeName;
    public LocalDate scheduledDate;
    public String mealSlot;

    public MealPlanResponse(UUID id, UUID groupId, UUID recipeId, String recipeName, LocalDate scheduledDate, String mealSlot) {
        this.id = id;
        this.groupId = groupId;
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.scheduledDate = scheduledDate;
        this.mealSlot = mealSlot;
    }
}