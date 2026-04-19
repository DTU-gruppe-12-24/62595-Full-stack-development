<script setup lang="ts">

import WeekCalendar from "@/components/AppCalendar.vue"

function handleCellClick(data: {day: Date, slot: string}) {

  console.log("Clicked cell:", data)

}

</script>

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

    <WeekCalendar
        @cell-click="handleCellClick"
    />

    <div v-else-if="mealPlans.length > 0">
      <h2>Planned recipes</h2>

      <ul>
        <li v-for="mealPlan in mealPlans" :key="mealPlan.id">
          {{ mealPlan.recipe.name }} - {{ mealPlan.mealSlot }}
        </li>
      </ul>
    </div>

    <p v-else-if="selectedDate">
      No planned recipes for this date.
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue"
import AppCalendar from "@/components/AppCalendar.vue"
import type { MealPlan } from "@/model/MealPlan"
import { getMealPlansByDate } from "@/services/MealPlanService"

const selectedDate = ref<string | null>(null)
const mealPlans = ref<MealPlan[]>([])
const isLoading = ref(false)
const errorMessage = ref("")

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
</script>

<style scoped>

.page {
  padding: 30px;
}

</style>
