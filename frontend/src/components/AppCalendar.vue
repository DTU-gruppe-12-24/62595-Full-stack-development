<script setup lang="ts">
import { ref, computed } from "vue"
import WeekSelector from "@/components/WeekSelector.vue"

defineEmits(["cell-click"])

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
            @click="$emit('cell-click', { day, slot })"
        >
          <span class="placeholder">+</span>
        </div>

      </template>

    </div>

  </div>
</template>

<style scoped>
.calendar {
  width: 100%;
  max-width: 900px;
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
  min-height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.cell:hover {
  background: var(--color-primary-soft);
}

.placeholder {
  opacity: 0.5;
  font-size: 20px;
}
</style>