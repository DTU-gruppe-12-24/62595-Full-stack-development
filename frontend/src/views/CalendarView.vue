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

    <p v-else-if="isLoading" class="muted-text">
      Loading meal plans...
    </p>

    <div v-if="selectedDateMealPlans.length > 0" :key="selectedDate" class="meal-plan-list">
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

      <AppButton @click="addMealPlan">
        Save
      </AppButton>
    </template>
  </AppDialog>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from "vue"
import AppCalendar from "@/components/AppCalendar.vue"
import AppButton from "@/components/AppButton.vue"
import AppDialog from "@/components/AppDialog.vue"
import type { MealPlan } from "@/model/MealPlan"
import {getMealPlansByDate, deleteMealPlan, createMealPlan, getMealPlansByRange} from "@/services/MealPlanService"
import { apiFetch } from "@/utilities/apiFetch"
import GroupSelector from "@/components/GroupSelector.vue"
import type { Group } from "@/model/Group"
import { showError } from "@/utilities/notifications"

const selectedDate = ref<string | undefined>(undefined)
const selectedMealSlot = ref("")
const weekMealPlans = ref<MealPlan[]>([])
const selectedDateMealPlans = ref<MealPlan[]>([])
const isLoading = ref(false)
const showAdd = ref(false)
const selectedRecipeId = ref("")
const recipes = ref<any[]>([])
const activeGroup = ref<Group | undefined>(undefined)
const groupId = computed(() => activeGroup.value?.id);

function formatDate(date: Date): string {
  return date.toISOString().slice(0, 10)
}

async function selectCell(payload: { day: Date; slot: string }) {
  const date = formatDate(payload.day)
  const mealSlot = payload.slot.toUpperCase()
  selectedDateMealPlans.value = []
  selectedRecipeId.value = ""
  showAdd.value = true
  try {

    if (!groupId.value) return showError("No active group selected.")

    try {
        isLoading.value = true
        selectedDateMealPlans.value = await getMealPlansByDate(
            groupId.value,
            date
        )
    } catch (error) {
        showError("Could not load meal plans.")
    } finally {
        isLoading.value = false
    }
  }
  finally {
    selectedMealSlot.value = mealSlot;
    selectedDate.value = date;
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
    return showError("Missing data.")
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
    showError("Could not create meal plan.")
  }
  await loadWeekMealPlans(new Date(selectedDate.value))
}

async function removeMealPlan(id: string) {
  try {
    await deleteMealPlan(id)
    selectedDateMealPlans.value = selectedDateMealPlans.value.filter(m => m.id !== id)
    weekMealPlans.value = weekMealPlans.value.filter(m => m.id !== id)
  } catch (error) {
    showError("Could not delete meal plan.")
  }
}

async function loadRecipes() {
  try {
    recipes.value = await apiFetch("/api/recipes")
  } catch (error) {
    showError("Could not load recipes.")
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
  if (!groupId.value) return

  const start = getWeekStart(date)
  const end = getWeekEnd(date)

  weekMealPlans.value = await getMealPlansByRange(groupId.value, start, end)
}

onMounted(() => {
  loadRecipes()
  loadWeekMealPlans()
})

watch(groupId, () => {
    loadWeekMealPlans()
});
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
