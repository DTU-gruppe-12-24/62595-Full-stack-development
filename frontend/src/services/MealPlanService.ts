import { apiFetch } from "@/utilities/apiFetch"
import type { MealPlan } from "@/model/MealPlan"

export async function getMealPlansByDate(groupId: string, date: string): Promise<MealPlan[]> {
    return await apiFetch<MealPlan[]>(`/api/meal-plans?groupId=${groupId}&date=${date}`)
}

export async function deleteMealPlan(id: string): Promise<void> {
    await apiFetch<void>(`/api/meal-plans/${id}`, "DELETE").catch((error) => {
        throw error
    });
}

export async function createMealPlan(groupId: string, recipeId: string, scheduledDate: string, mealSlot: string, cookerId?: string): Promise<void> {
    await apiFetch(
        "/api/meal-plans",
        "POST",
        {
            groupId,
            recipeId,
            scheduledDate,
            mealSlot,
            cookerId
        }
    )
}

export async function getMealPlansByRange(groupId: string, start: string, end: string): Promise<MealPlan[]> {
    return await apiFetch<MealPlan[]>(
        `/api/meal-plans/range?groupId=${groupId}&start=${start}&end=${end}`
    )
}

export async function updateMealPlan(
    id: string,
    scheduledDate: string,
    mealSlot: string
): Promise<MealPlan> {
    return await apiFetch(`/api/meal-plans/${id}`, "PUT", {
        scheduledDate,
        mealSlot
    })
}
