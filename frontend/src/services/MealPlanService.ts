import { apiFetch } from "@/utilities/apiFetch"
import type { MealPlan } from "@/model/MealPlan"

export async function getMealPlansByDate(groupId: string, date: string): Promise<MealPlan[]> {
    return await apiFetch<MealPlan[]>(`/api/meal-plans?groupId=${groupId}&date=${date}`)
}