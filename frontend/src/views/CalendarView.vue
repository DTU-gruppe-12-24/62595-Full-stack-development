<template>
  <div class="page">
    <h1>Calendar</h1>
    <div class="group-selector-wrapper">
      <GroupSelector v-model="activeGroup" persist />
    </div>

    <AppCalendar
        :meal-plans="weekMealPlans"
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

    <div v-else-if="selectedDateMealPlans.length > 0" class="meal-plan-list">
      <h4 class="section-title">Planned recipes</h4>

      <div
          v-for="mealPlan in selectedDateMealPlans"
          :key="mealPlan.id"
          class="meal-plan-item"
      >
        <div>
          <p class="meal-name">{{ mealPlan.recipeName }}</p>
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

      <AppButton
          v-if="selectedRecipeId"
          @click="addMealPlan"
      >
        Save
      </AppButton>
    </template>
  </AppDialog>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from "vue"
import AppCalendar from "@/components/AppCalendar.vue"
import AppButton from "@/components/AppButton.vue"
import AppDialog from "@/components/AppDialog.vue"
import type { MealPlan } from "@/model/MealPlan"
import {
  getMealPlansByDate,
  deleteMealPlan,
  createMealPlan,
  getMealPlansByRange,
  updateMealPlan
} from "@/services/MealPlanService"
import { apiFetch } from "@/utilities/apiFetch"
import GroupSelector from "@/components/GroupSelector.vue"
import type { Group } from "@/model/Group"

const selectedDate = ref<string | null>(null)
const selectedMealSlot = ref("")
const weekMealPlans = ref<MealPlan[]>([])
const selectedDateMealPlans = ref<MealPlan[]>([])
const isLoading = ref(false)
const errorMessage = ref("")
const showAdd = ref(false)
const selectedRecipeId = ref("")
const recipes = ref<any[]>([])
const activeGroup = ref<Group | undefined>(undefined)
const groupId = activeGroup.value?.id

function formatDate(date: Date): string {
  return date.toISOString().slice(0, 10)
}

async function selectCell(payload: { day: Date; slot: string }) {
  selectedDate.value = formatDate(payload.day)
  selectedMealSlot.value = payload.slot.toUpperCase()
  errorMessage.value = ""
  selectedDateMealPlans.value = []
  selectedRecipeId.value = ""
  showAdd.value = true

  const groupId = activeGroup.value?.id

  if (!groupId) {
    errorMessage.value = "No active group selected."
    return
  }

  try {
    isLoading.value = true
    selectedDateMealPlans.value = await getMealPlansByDate(
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
  const groupId = activeGroup.value?.id

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

    selectedDateMealPlans.value = await getMealPlansByDate(
        groupId,
        selectedDate.value
    )

    showAdd.value = false
    selectedRecipeId.value = ""
  } catch (error) {
    console.error(error)
    errorMessage.value = "Could not create meal plan."
  }
  await loadWeekMealPlans(new Date(selectedDate.value))
}

async function removeMealPlan(id: string) {
  try {
    await deleteMealPlan(id)

    selectedDateMealPlans.value = selectedDateMealPlans.value.filter(
        mealPlan => mealPlan.id !== id
    )

    if (selectedDate.value) {
      await loadWeekMealPlans(new Date(selectedDate.value))
    }
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

function getWeekStart(date: Date): string {
  const d = new Date(date)
  const day = d.getDay()
  const diff = d.getDate() - (day === 0 ? 6 : day - 1)
  d.setDate(diff)
  return d.toISOString().slice(0, 10)
}

function getWeekEnd(date: Date): string {
  const d = new Date(date)
  const day = d.getDay()
  const diff = d.getDate() - (day === 0 ? 6 : day - 1) + 6
  d.setDate(diff)
  return d.toISOString().slice(0, 10)
}

async function loadWeekMealPlans(date: Date = new Date()) {
  const groupId = activeGroup.value?.id

  if (!groupId) {
    return
  }

  const start = getWeekStart(date)
  const end = getWeekEnd(date)

  weekMealPlans.value = await getMealPlansByRange(groupId, start, end)
}

onMounted(() => {
  loadRecipes()
  loadWeekMealPlans()
})

watch(activeGroup, () => {
  loadWeekMealPlans()
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

.group-selector-wrapper {
  max-width: 200px;
  margin-bottom: 20px;
}
</style>
