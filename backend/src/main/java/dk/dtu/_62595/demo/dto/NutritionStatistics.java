package dk.dtu._62595.demo.dto;

public record NutritionStatistics(
        Double calories,
        Double protein,
        Double carbohydrates,
        Double fat,
        Double saturatedFat,
        Double sugars,
        Double salt
) {
    public NutritionStatistics {
        calories = calories != null ? calories : 0.0;
        protein = protein != null ? protein : 0.0;
        carbohydrates = carbohydrates != null ? carbohydrates : 0.0;
        fat = fat != null ? fat : 0.0;
        saturatedFat = saturatedFat != null ? saturatedFat : 0.0;
        sugars = sugars != null ? sugars : 0.0;
        salt = salt != null ? salt : 0.0;
    }
}