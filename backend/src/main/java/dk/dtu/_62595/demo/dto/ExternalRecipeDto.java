package dk.dtu._62595.demo.dto;

import java.util.List;

public record ExternalRecipeDto(
        String name,
        String description,
        String instructions,
        String mealType,
        Integer servings,
        Integer prepTimeMinutes,
        String imageUrl,
        List<RecipeIngredientDto> ingredients,
        String source
) {}
