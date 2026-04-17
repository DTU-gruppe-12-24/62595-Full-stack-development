import type { Group } from "./Group";
import type { Ingredient } from "./Ingredient";
import type { Unit } from "./RecipeIngredient";

export type ShoppingList = {
	id: string;
	group: Group;
	ingredient: Ingredient;
    amount: number;
    unit: Unit;
	isBought: boolean;
};