package dk.dtu._62595.demo.dto;

import java.time.LocalDate;

public record UpdateRecipeRequest(
        String name,
        String description,
        String instructions,
        String mealType,
        Integer servings,
        Integer prepTimeMinutes,
        String imageUrl,
        LocalDate lastMade
) {}
