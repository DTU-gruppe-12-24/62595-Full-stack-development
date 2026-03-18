package dk.dtu._62595.demo.dto;

import java.time.LocalDate;
import java.util.List;

public record UpdateRecipeRequest(
        String name,
        String description,
        String instructions,
        String mealType,
        Integer servings,
        Integer prepTimeMinutes,
        String imageUrl,
        LocalDate lastMade,
        List<RecipeIngredientRequest> ingredients
) {}
