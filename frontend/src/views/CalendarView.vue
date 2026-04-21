<template>
  <div class="page">
    <h1>Calendar</h1>

    <AppCalendar @select="selectDate" />

    <p v-if="selectedDate">
      Selected: {{ selectedDate }}
    </p>

    <p v-if="isLoading">Loading meal plans...</p>

    <p v-else-if="errorMessage">
      {{ errorMessage }}
    </p>

    <div v-else-if="mealPlans.length > 0">
      <h2>Planned recipes</h2>

      <ul>
        <li v-for="mealPlan in mealPlans" :key="mealPlan.id">
          {{ mealPlan.recipe.name }} - {{ mealPlan.mealSlot }}
          <button @click="removeMealPlan(mealPlan.id)">Remove</button>
        </li>
      </ul>
    </div>

    <p v-else-if="selectedDate">
      No planned recipes for this date.
    </p>
  </div>

  <button v-if="selectedDate" @click="showAdd = !showAdd">
    Add recipe
  </button>

  <div v-if="showAdd">
    <select v-model="selectedRecipeId">
      <option disabled value="">Select recipe</option>
      <option v-for="recipe in recipes" :key="recipe.id" :value="recipe.id">
        {{ recipe.name }}
      </option>
    </select>

    <button @click="addMealPlan">Save</button>
  </div>
</template>

<script setup lang="ts">
import AppCalendar from "@/components/AppCalendar.vue"
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

async function selectDate(date: string) {
  selectedDate.value = date
  errorMessage.value = ""
  mealPlans.value = []

  const groupId = localStorage.getItem("activeGroupId")

  if (!groupId) {
    errorMessage.value = "No active group selected."
    return
  }

  try {
    isLoading.value = true
    mealPlans.value = await getMealPlansByDate(groupId, date)
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
</style>