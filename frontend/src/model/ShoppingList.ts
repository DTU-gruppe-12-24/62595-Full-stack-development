import type { Group } from "./Group";
import type { Ingredient } from "./Ingredient";

export type ShoppingList = {
	id: string;
	group: Group;
	ingredient: Ingredient;
	amount: number;
	isBought: boolean;
};