package dk.dtu._62595.demo.dto;

public record CreateIngredientRequest(
        String name,
        Float calories,
        Float protein,
        Float carbohydrates,
        Float fat,
        Float saturatedFat,
        Float sugars,
        Float salt,
        Float price
) {}
