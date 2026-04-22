<template>
  <div class="page">
    <h1>Calendar</h1>

    <AppCalendar @cell-click="selectCell" />
    <div class="content">
      <div class="sidebar">
        <AppCard v-if="selectedDate" class="info-card">
          <h3 class="card-title">Selected slot</h3>
          <p class="selected-date">{{ selectedDate }}</p>
          <p v-if="selectedMealSlot" class="selected-slot">{{ selectedMealSlot }}</p>

          <p v-if="errorMessage" class="error-text">
            {{ errorMessage }}
          </p>

          <p v-else-if="isLoading" class="muted-text">
            Loading meal plans...
          </p>

          <div v-else-if="mealPlans.length > 0" class="meal-plan-list">
            <h4 class="section-title">Planned recipes</h4>

            <div
                v-for="mealPlan in mealPlans"
                :key="mealPlan.id"
                class="meal-plan-item"
            >
              <div>
                <p class="meal-name">{{ mealPlan.recipe.name }}</p>
                <p class="meal-slot">{{ mealPlan.mealSlot }}</p>
              </div>

              <AppButton @click="removeMealPlan(mealPlan.id)">
                Remove
              </AppButton>
            </div>
          </div>

          <p v-else class="muted-text">
            No planned recipes for this date.
          </p>

          <div class="add-section">
            <h4 class="section-title">Add recipe</h4>

            <select v-model="selectedRecipeId" class="recipe-select">
              <option disabled value="">Select recipe</option>
              <option v-for="recipe in recipes" :key="recipe.id" :value="recipe.id">
                {{ recipe.name }}
              </option>
            </select>

            <div class="actions">
              <AppButton @click="addMealPlan">Save</AppButton>
            </div>
          </div>
        </AppCard>
      </div>
    </div>
  </div>

</template>

<script setup lang="ts">
import AppCalendar from "@/components/AppCalendar.vue"
import AppCard from "@/components/AppCard.vue"
import AppButton from "@/components/AppButton.vue"
import type { MealPlan } from "@/model/MealPlan"
import { ref, onMounted } from "vue"
import { getMealPlansByDate, deleteMealPlan, createMealPlan } from "@/services/MealPlanService"
import { apiFetch } from "@/utilities/apiFetch"


const selectedDate = ref<string | null>(null)
const mealPlans = ref<MealPlan[]>([])
const isLoading = ref(false)
const errorMessage = ref("")
const showAdd = ref(false)
const selectedRecipeId = ref("")
const recipes = ref<any[]>([])
const selectedMealSlot = ref("")

function formatDate(date: Date): string {
  return date.toISOString().slice(0, 10)
}

async function selectCell(payload: { day: Date; slot: string }) {
  selectedDate.value = formatDate(payload.day)
  selectedMealSlot.value = payload.slot.toUpperCase()
  errorMessage.value = ""
  mealPlans.value = []
  showAdd.value = true

  const groupId = localStorage.getItem("activeGroupId")

  if (!groupId) {
    errorMessage.value = "No active group selected."
    return
  }

  try {
    isLoading.value = true
    mealPlans.value = await getMealPlansByDate(groupId, selectedDate.value)
  } catch (error) {
    console.error(error)
    errorMessage.value = "Could not load meal plans."
  } finally {
    isLoading.value = false
  }
}

async function addMealPlan() {
  const groupId = localStorage.getItem("activeGroupId")

  if (!groupId || !selectedDate.value || !selectedRecipeId.value) {
    errorMessage.value = "Missing data."
    return
  }

  try {
    await createMealPlan(groupId, selectedRecipeId.value, selectedDate.value)
    mealPlans.value = await getMealPlansByDate(groupId, selectedDate.value)
    showAdd.value = false
    selectedRecipeId.value = ""
  } catch (error) {
    console.error(error)
    errorMessage.value = "Could not create meal plan."
  }
}

async function removeMealPlan(id: string) {
  try {
    await deleteMealPlan(id)
    mealPlans.value = mealPlans.value.filter(mealPlan => mealPlan.id !== id)
  } catch (error) {
    console.error(error)
    errorMessage.value = "Could not delete meal plan."
  }
}

async function loadRecipes() {
  try {
    recipes.value = await apiFetch("/api/recipes")
  } catch (error) {
    console.error(error)
    errorMessage.value = "Could not load recipes."
  }
}

onMounted(() => {
  loadRecipes()
})

</script>

<style scoped>
.page {
  padding: 20px;
}

.content {
  margin-top: 24px;
}

.sidebar {
  max-width: 360px;
}

.info-card {
  padding: 20px;
}

.card-title {
  margin: 0 0 12px;
  font-size: 20px;
}

.selected-date {
  margin: 0;
  font-weight: 600;
  font-size: 18px;
}

.selected-slot {
  margin: 4px 0 16px;
  color: #666;
  font-size: 14px;
  text-transform: capitalize;
}

.section-title {
  margin: 20px 0 10px;
  font-size: 16px;
}

.muted-text {
  color: #666;
  margin: 12px 0;
}

.error-text {
  color: #c62828;
  margin: 12px 0;
}

.meal-plan-list {
  margin-top: 12px;
}

.meal-plan-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.meal-name {
  margin: 0;
  font-weight: 600;
}

.meal-slot {
  margin: 4px 0 0;
  font-size: 14px;
  color: #666;
}

.add-section {
  margin-top: 20px;
}

.recipe-select {
  width: 100%;
  padding: 10px 12px;
  margin-top: 8px;
  border: 1px solid #ddd;
  border-radius: 10px;
  background: white;
}

.actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>