package dk.dtu._62595.demo.dto;

import java.time.LocalDate;
import java.util.UUID;

public class CreateRecipeRequest {

    private String name;
    private String description;
    private String instructions;
    private String mealType;
    private int servings;
    private int prepTimeMinutes;
    private String imageUrl;
    private LocalDate lastMade;
    private UUID groupId;

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getInstructions() { return instructions; }
    public String getMealType() { return mealType; }
    public int getServings() { return servings; }
    public int getPrepTimeMinutes() { return prepTimeMinutes; }
    public String getImageUrl() { return imageUrl; }
    public LocalDate getLastMade() { return lastMade; }
    public UUID getGroupId() { return groupId; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public void setMealType(String mealType) { this.mealType = mealType; }
    public void setServings(int servings) { this.servings = servings; }
    public void setPrepTimeMinutes(int prepTimeMinutes) { this.prepTimeMinutes = prepTimeMinutes; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setLastMade(LocalDate lastMade) { this.lastMade = lastMade; }
    public void setGroupId(UUID groupId) { this.groupId = groupId; }
} 