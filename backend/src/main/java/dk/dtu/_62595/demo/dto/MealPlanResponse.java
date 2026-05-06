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
    UUID cookerId;
    String cookerName;

    public MealPlanResponse(UUID id, UUID groupId, UUID recipeId, String recipeName, LocalDate scheduledDate, String mealSlot, UUID cookerId, String cookerName) {
        this.id = id;
        this.groupId = groupId;
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.scheduledDate = scheduledDate;
        this.mealSlot = mealSlot;
        this.cookerId = cookerId;
        this.cookerName = cookerName;
    }
}
