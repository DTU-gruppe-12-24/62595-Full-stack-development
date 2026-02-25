import type { Group } from "./Group";
import type { Recipe } from "./Recipe";

export type MealPlan = {
	id: string;
	group: Group;
	recipe: Recipe;
	scheduledDate: Date;
	mealSlot: string;
};