package dk.dtu._62595.demo.dto;

import java.util.UUID;

public record RecipeIngredientDto(
        UUID ingredientId,
        String ingredientName,
        Float amount,
        String unit
) {}
