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

    <div v-if="selectedDateMealPlans.length > 0" :key="selectedDate" class="meal-plan-section">
      <h4 class="section-title">Planned recipes</h4>

      <div v-for="mealPlan in selectedDateMealPlans" :key="mealPlan.id" class="meal-plan-card">
        <button class="remove-entry-btn" @click="removeMealPlan(mealPlan.id)" title="Remove recipe">
          ✕
        </button>

        <div class="meal-details">
          <p class="meal-name">{{ mealPlan.recipeName }}</p>
          <AppDropdown
              label="Assigned Cooker"
              :values="memberOptions"
              v-model="mealPlan.cookerId"
          />
        </div>
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
import { ref, onMounted, computed, watch } from "vue"
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
import {showError, showSuccess} from "@/utilities/notifications.ts";
import AppDropdown from "@/components/AppDropdown.vue";

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
      const plans = await getMealPlansByDate(groupId.value, date)

      selectedDateMealPlans.value = plans.filter(
          mealPlan => mealPlan.mealSlot === mealSlot
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
  if (!activeGroup.value?.id || !selectedDate.value || !selectedMealSlot.value) return;
  const gId = activeGroup.value.id;

  try {
    isLoading.value = true;

    const updatePromises = selectedDateMealPlans.value.map(meal => {
      return apiFetch(`/api/meal-plans/${meal.id}/cooker?userId=${meal.cookerId || ''}`, 'PATCH');
    });
    await Promise.all(updatePromises);

    if (selectedRecipeId.value) {
      await createMealPlan(
          gId,
          selectedRecipeId.value,
          selectedDate.value,
          selectedMealSlot.value
      );
    }
    const plans = await getMealPlansByDate(gId, selectedDate.value)

    selectedDateMealPlans.value = plans.filter(
        mealPlan => mealPlan.mealSlot === selectedMealSlot.value
    )

    await loadWeekMealPlans(new Date(selectedDate.value));
    showAdd.value = false;
    selectedRecipeId.value = "";
    showSuccess("Changes saved successfully");
  } catch (error) {
    showError("Could not save changes.");
  } finally {
    isLoading.value = false;
  }
}

async function removeMealPlan(id: string) {
  try {
    await deleteMealPlan(id)

    selectedDateMealPlans.value = selectedDateMealPlans.value.filter(
        mealPlan => mealPlan.id !== id
    )

    weekMealPlans.value = weekMealPlans.value.filter(
        mealPlan => mealPlan.id !== id
    )

    if (selectedDate.value) {
      await loadWeekMealPlans(new Date(selectedDate.value))
    }
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

const groupMembers = ref<any[]>([])

watch(activeGroup, (newGroup) => {
  if (newGroup) {
    loadWeekMealPlans()
    loadGroupMembers()
  }
})

async function loadGroupMembers() {
  if (!activeGroup.value) return
  try {
    groupMembers.value = await apiFetch<any[]>(`/api/group/${activeGroup.value.id}/members`)
  } catch (e) {
    console.error("Failed to load members", e)
  }
}

const memberOptions = computed(() => {
  return groupMembers.value.map(m => ({
    id: m.user.id,
    name: m.user.name
  }))
})

onMounted(() => {
  loadRecipes()
  loadWeekMealPlans()
})

watch(groupId, () => {
    loadWeekMealPlans()
});

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

.recipe-select {
  width: 100%;
  padding: 8px;
  margin-top: 8px;
}

.group-selector-wrapper {
  max-width: 200px;
  margin-bottom: 20px;
}
.add-section {
  border-top: 1px solid #eee;
  padding-top: 16px;
  margin-top: 16px;
}

.meal-plan-card {
  position: relative;
  display: flex;
  flex-direction: column;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 12px;
  transition: border-color 0.2s;
}

.meal-plan-card:hover {
  border-color: #dee2e6;
}

.meal-name {
  font-weight: 700;
  font-size: 1.1rem;
  margin: 0 0 12px 0;
  color: #333;
  padding-right: 24px;
}

.remove-entry-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  color: #adb5bd;
  font-size: 18px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
}

.remove-entry-btn:hover {
  background-color: #fee2e2;
  color: #dc2626;
}

.meal-details :deep() {
  margin-top: 4px;
}
</style>
