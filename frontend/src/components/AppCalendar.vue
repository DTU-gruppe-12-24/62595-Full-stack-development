<script setup lang="ts">
import { ref, computed } from "vue"
import WeekSelector from "@/components/WeekSelector.vue"
import type { MealPlan } from "@/model/MealPlan"

defineEmits(["cell-click"])

const props = defineProps<{
  mealPlans?: MealPlan[]
}>()

const mealSlots = ["Breakfast", "Lunch", "Dinner"]

const currentMonday = ref(getInitialMonday())

function getInitialMonday() {
  const d = new Date()
  const day = d.getDay()
  const diff = d.getDate() - (day === 0 ? 6 : day - 1)
  const monday = new Date(d.setDate(diff))
  monday.setHours(0, 0, 0, 0)
  return monday
}

const weekDays = computed(() => {
  return Array.from({ length: 7 }, (_, i) => {
    const d = new Date(currentMonday.value)
    d.setDate(d.getDate() + i)
    return d
  })
})

function formatDay(day: Date) {
  return day.toLocaleDateString("en", {
    weekday: "short",
    day: "numeric"
  })
}

function formatDate(date: Date): string {
  return date.toISOString().slice(0, 10)
}

function normalizeSlot(slot: string): string {
  return slot.toUpperCase()
}

function getMealPlan(day: Date, slot: string): MealPlan | undefined {
  const date = formatDate(day)
  const normalizedSlot = normalizeSlot(slot)

  return props.mealPlans?.find(mealPlan => {
    const mealDate =
        mealPlan.scheduledDate instanceof Date
            ? formatDate(mealPlan.scheduledDate)
            : String(mealPlan.scheduledDate)

    return mealDate === date && mealPlan.mealSlot === normalizedSlot
  })
}

function getShortName(name?: string): string {
  if (!name) return ""
  return name.length > 10 ? name.slice(0, 10) + "..." : name
}
</script>

<template>
  <div class="calendar">
    <div class="calendar-header">
      <WeekSelector v-model="currentMonday" />
    </div>

    <div class="calendar-grid">
      <div></div>

      <div
          v-for="day in weekDays"
          :key="day.toISOString()"
          class="day-header"
      >
        {{ formatDay(day) }}
      </div>

      <template v-for="slot in mealSlots" :key="slot">
        <div class="slot-label">
          {{ slot }}
        </div>

        <div
            v-for="day in weekDays"
            :key="slot + day.toISOString()"
            class="cell"
            :class="{ 'has-plan': getMealPlan(day, slot) }"
            @click="$emit('cell-click', { day, slot })"
        >
          <span v-if="getMealPlan(day, slot)" class="meal-name">
            {{ getShortName((getMealPlan(day, slot) as any)?.recipe?.name ?? (getMealPlan(day, slot) as any)?.recipeName ?? "Recipe") }}
          </span>

          <span v-else class="placeholder">+</span>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.calendar {
  width: 100%;
  max-width: 1100px;
  margin: auto;
}

.calendar-header {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}

.calendar-grid {
  display: grid;
  grid-template-columns: 120px repeat(7, 1fr);
}

.day-header {
  text-align: center;
  font-weight: 600;
  padding: 10px;
}

.slot-label {
  padding: 12px;
  font-weight: 600;
}

.cell {
  border: 1px solid #ddd;
  min-height: 90px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  padding: 8px;
  text-align: center;
}

.cell:hover {
  background: var(--color-primary-soft);
}

.cell.has-plan {
  background: var(--color-primary-soft);
  border-color: var(--color-primary);
}

.placeholder {
  opacity: 0.5;
  font-size: 20px;
}

.meal-name {
  font-weight: 600;
  font-size: 14px;
}
</style>