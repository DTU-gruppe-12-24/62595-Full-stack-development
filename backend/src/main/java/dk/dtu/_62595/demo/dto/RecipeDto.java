package dk.dtu._62595.demo.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record RecipeDto(
        UUID id,
        String ownerName,
        UUID ownerId,
        UUID groupId,
        String name,
        String description,
        String instructions,
        String mealType,
        Integer servings,
        Integer prepTimeMinutes,
        String imageUrl,
        LocalDate lastMade,
        List<RecipeIngredientDto> ingredients
) {}
