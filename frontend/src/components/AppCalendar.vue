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

function formatDayFull(day: Date) {
  return day.toLocaleDateString("en", {
    weekday: "long",
    month: "short",
    day: "numeric"
  })
}
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

    <!-- Mobile: vertical layout (days as rows) -->
    <div class="mobile-calendar">
      <div
        v-for="day in weekDays"
        :key="'mobile-' + day.toISOString()"
        class="mobile-day"
      >
        <div class="mobile-day-header">{{ formatDayFull(day) }}</div>
        <div
          v-for="slot in mealSlots"
          :key="'mobile-' + slot + day.toISOString()"
          class="mobile-cell"
          @click="$emit('cell-click', { day, slot })"
        >
          <span class="mobile-slot-label">{{ slot }}</span>
          <span class="placeholder">+</span>
        </div>
      </div>
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

/* ── Desktop grid ── */
.calendar-scroll {
  width: 100%;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.calendar-grid {
  display: grid;
  grid-template-columns: 90px repeat(7, minmax(80px, 1fr));
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