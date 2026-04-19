import { apiFetch } from "@/utilities/apiFetch"

export interface Statistic {
    totalMealsPlanned: number;
    recipeCounts: Record<string, number>;
}

export interface NutritionStatistics {
    calories: number;
    protein: number;
    carbohydrates: number;
    fat: number;
    saturatedFat: number;
    sugars: number;
    salt: number;
}

export interface StatisticsSummary {
    general: Statistic;
    nutrition: NutritionStatistics;
}

export async function getStatistics(groupId: string, start?: string, end?: string): Promise<StatisticsSummary> {
    let url = `/api/statistics/${groupId}`;
    if (start && end) {
        url += `?start=${start}&end=${end}`;
    }
    return apiFetch(url);
}