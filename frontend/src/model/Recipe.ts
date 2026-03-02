import type { Group } from "./Group";
import type { User } from "./User";

export type Recipe = {
	id: string;
	owner: User;
	group?: Group;
	name: string;
	description?: string;
	instructions?: string;
	mealType?: string;
	servings?: number;
	prepTimeMinutes?: number;
	imageUrl?: string;
	lastMade?: Date;
};