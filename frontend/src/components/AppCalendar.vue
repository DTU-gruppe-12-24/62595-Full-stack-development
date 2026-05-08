<script setup lang="ts">
import { ref, computed, watch } from "vue"
import WeekSelector from "@/components/WeekSelector.vue"
import type { MealPlan } from "@/model/MealPlan"

const emits = defineEmits(["cell-click", "week-updated"])

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
function formatDayFull(day: Date) {
  return day.toLocaleDateString("en", {
    weekday: "long",
    month: "short",
    day: "numeric"
  })
}

function formatDate(date: Date): string {
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
}

function normalizeSlot(slot: string): string {
  return slot.toUpperCase()
}

function getMealPlan(day: Date, slot: string): MealPlan | undefined {
  const date = formatDate(day)
  const normalizedSlot = normalizeSlot(slot)

  return props.mealPlans?.find(mealPlan => {
    return (
        mealPlan.scheduledDate === date &&
        mealPlan.mealSlot === normalizedSlot
    )
  })
}

function getShortName(name?: string): string {
  if (!name) return ""
  return name.length > 10 ? name.slice(0, 10) + "..." : name
}

watch(currentMonday, () => {
    emits("week-updated", currentMonday.value);
})
</script>

<template>
  <div class="calendar">
    <div class="calendar-header">
      <WeekSelector v-model="currentMonday" />
    </div>

    <!-- Desktop: horizontal grid (days as columns) -->
    <div class="calendar-scroll desktop-calendar">
      <div class="calendar-grid">
      	<div></div>
	    <div
	      v-for="day in weekDays"
	      :key="formatDate(day)"
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
	          :key="slot + formatDate(day)"
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

    <!-- Mobile: vertical layout (days as rows) -->
    <div class="mobile-calendar">
      <div
        v-for="day in weekDays"
        :key="'mobile-' + formatDate(day)"
        class="mobile-day"
      >
        <div class="mobile-day-header">{{ formatDayFull(day) }}</div>
        <div
          v-for="slot in mealSlots"
          :key="'mobile-' + slot + formatDate(day)"
          class="mobile-cell"
          :class="{ 'has-plan': getMealPlan(day, slot) }"
          @click="$emit('cell-click', { day, slot })"
        >
          <span class="mobile-slot-label">{{ slot }}</span>
          <span v-if="getMealPlan(day, slot)" class="meal-name">
            {{ getShortName((getMealPlan(day, slot) as any)?.recipe?.name ?? (getMealPlan(day, slot) as any)?.recipeName ?? "Recipe") }}
          </span>
          <span v-else class="placeholder">+</span>
        </div>
      </div>
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

/* ── Desktop grid ── */
.calendar-scroll {
  width: 100%;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.calendar-grid {
  display: grid;
  grid-template-columns: 120px repeat(7, 1fr);
  min-width: 650px;
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

.has-plan {
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

/* ── Mobile vertical layout ── */
.mobile-calendar {
  display: none;
  flex-direction: column;
  gap: 12px;
}

.mobile-day {
  border: 1px solid var(--color-border, #ddd);
  border-radius: var(--radius-md, 10px);
  overflow: hidden;
}

.mobile-day-header {
  background: var(--color-primary);
  color: white;
  font-weight: 700;
  padding: 10px 14px;
  font-size: 0.95rem;
}

.mobile-cell {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px;
  border-top: 1px solid var(--color-border, #eee);
  cursor: pointer;
  transition: background 0.15s;
}

.mobile-cell:hover {
  background: var(--color-primary-soft);
}

.mobile-slot-label {
  font-weight: 500;
  color: var(--color-text);
}

@media (max-width: 640px) {
  .desktop-calendar {
    display: none;
  }

  .mobile-calendar {
    display: flex;
  }
}
</style>