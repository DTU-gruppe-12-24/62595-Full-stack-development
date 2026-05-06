package dk.dtu._62595.demo.dto;

import java.time.LocalDate;
import java.util.UUID;

public class CreateMealPlanRequest {
    public UUID groupId;
    public UUID recipeId;
    public UUID cookerId;
    public LocalDate scheduledDate;
    public String mealSlot;
}
