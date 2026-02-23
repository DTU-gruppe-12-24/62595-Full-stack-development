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
	unit: string;
};