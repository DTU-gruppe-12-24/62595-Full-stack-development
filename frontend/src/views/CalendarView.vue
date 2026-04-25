<template>
  <div class="page">
    <h1>Calendar</h1>

    <AppCalendar
        :meal-plans="mealPlans"
        @cell-click="selectCell"
    />
  </div>

  <AppDialog
      v-model="showAdd"
      title="Selected date"
      width="420px"
  >
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
    </div>

    <template #footer>
      <AppButton @click="showAdd = false">
        Cancel
      </AppButton>

      <AppButton @click="addMealPlan">
        Save
      </AppButton>
    </template>
  </AppDialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue"
import AppCalendar from "@/components/AppCalendar.vue"
import AppButton from "@/components/AppButton.vue"
import AppDialog from "@/components/AppDialog.vue"
import type { MealPlan } from "@/model/MealPlan"
import {
  getMealPlansByDate,
  deleteMealPlan,
  createMealPlan
} from "@/services/MealPlanService"
import { apiFetch } from "@/utilities/apiFetch"

const selectedDate = ref<string | null>(null)
const selectedMealSlot = ref("")
const mealPlans = ref<MealPlan[]>([])
const isLoading = ref(false)
const errorMessage = ref("")
const showAdd = ref(false)
const selectedRecipeId = ref("")
const recipes = ref<any[]>([])

function formatDate(date: Date): string {
  return date.toISOString().slice(0, 10)
}

async function selectCell(payload: { day: Date; slot: string }) {
  selectedDate.value = formatDate(payload.day)
  selectedMealSlot.value = payload.slot.toUpperCase()
  errorMessage.value = ""
  mealPlans.value = []
  selectedRecipeId.value = ""
  showAdd.value = true

  const groupId = localStorage.getItem("activeGroupId")

  if (!groupId) {
    errorMessage.value = "No active group selected."
    return
  }

  try {
    isLoading.value = true
    mealPlans.value = await getMealPlansByDate(
        groupId,
        selectedDate.value
    )
  } catch (error) {
    console.error(error)
    errorMessage.value = "Could not load meal plans."
  } finally {
    isLoading.value = false
  }
}

async function addMealPlan() {
  const groupId = localStorage.getItem("activeGroupId")

  if (
      !groupId ||
      !selectedDate.value ||
      !selectedRecipeId.value ||
      !selectedMealSlot.value
  ) {
    errorMessage.value = "Missing data."
    return
  }

  try {
    await createMealPlan(
        groupId,
        selectedRecipeId.value,
        selectedDate.value,
        selectedMealSlot.value
    )

    mealPlans.value = await getMealPlansByDate(
        groupId,
        selectedDate.value
    )

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
    mealPlans.value = mealPlans.value.filter(
        m => m.id !== id
    )
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

.selected-date {
  font-weight: 600;
  font-size: 18px;
}

.selected-slot {
  margin-bottom: 12px;
  color: #666;
}

.section-title {
  margin: 16px 0 8px;
}

.muted-text {
  color: #666;
}

.error-text {
  color: red;
}

.meal-plan-item {
  display: flex;
  justify-content: space-between;
  margin: 8px 0;
}

.recipe-select {
  width: 100%;
  padding: 8px;
  margin-top: 8px;
}
</style>