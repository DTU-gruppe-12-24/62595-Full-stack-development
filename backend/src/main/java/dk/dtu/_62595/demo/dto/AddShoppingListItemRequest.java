package dk.dtu._62595.demo.dto;

public record AddShoppingListItemRequest(
        String ingredientName,
        float amount,
        String unit
) { }
