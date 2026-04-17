import type { Ingredient } from "./Ingredient";
import type { Recipe } from "./Recipe";

export type RecipeIngredientId = {
	recipeId: string;
	ingredientId: string;
};

export type RecipeIngredient = {
	id: RecipeIngredientId;
	recipe: Recipe;
	ingredient: Ingredient;
	amount: number;
	unit: Unit;
};

export enum Unit {
    GRAM = "g",
	KILOGRAM = "kg",
	MILLILITER = "mL",
	DECILITER = "dL",
	LITER = "L",
	TABLESPOON = "tbsp",
	TEASPOON = "tsp",
	PINCH = "pinch",
	POUND = "lb",
	OUNCE = "oz",
	CUP = "cup",
    PIECE = "piece",
	NOTHING = ""
}