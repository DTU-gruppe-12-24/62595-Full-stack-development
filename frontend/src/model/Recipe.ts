import type { Group } from "./Group"

export type RecipeIngredient = {
  ingredientId: string
  ingredientName: string
  amount?: number
  unit?: string
}

export type Recipe = {
  id: string
  ownerName: string
  ownerId: string
  groupId?: string
  groupName?: string
  name: string
  description?: string
  instructions?: string
  mealType?: string
  servings?: number
  prepTimeMinutes?: number
  imageUrl?: string
  lastMade?: string
  ingredients: RecipeIngredient[]
}
