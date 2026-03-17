package dk.dtu._62595.demo.dto;

import java.util.UUID;

public record ShoppingListItemDto(
        UUID id,
        UUID ingredientId,
        String ingredientName,
        float amount,
        String unit,
        boolean isBought
) {}
