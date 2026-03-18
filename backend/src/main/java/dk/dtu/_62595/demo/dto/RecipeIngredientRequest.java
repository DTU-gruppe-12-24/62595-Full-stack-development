package dk.dtu._62595.demo.dto;

public record RecipeIngredientRequest(
        String ingredientName,
        Float amount,
        String unit
) {}
