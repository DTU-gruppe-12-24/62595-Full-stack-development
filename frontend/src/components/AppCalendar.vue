<template>

  <div class="calendar">

    <div class="header">

      <button @click="prevMonth">
        ←
      </button>

      <h3>
        {{ monthName }} {{ year }}
      </h3>

      <button @click="nextMonth">
        →
      </button>

    </div>

    <div class="grid">

      <div
          v-for="day in days"
          :key="day"
          class="day-name"
      >
        {{ day }}
      </div>

      <div
          v-for="date in calendarDays"
          :key="date.key"
          class="day"
          :class="{ today: date.isToday }"
          @click="$emit('select', date.date)"
      >
        {{ date.day }}
      </div>

    </div>

  </div>

</template>

<script setup lang="ts">

import { ref, computed } from 'vue'

const today = new Date()

const current = ref(new Date())

const year = computed(() => current.value.getFullYear())

const month = computed(() => current.value.getMonth())

const monthName = computed(() =>
    current.value.toLocaleString('default', { month: 'long' })
)

const days = ['Mon','Tue','Wed','Thu','Fri','Sat','Sun']

const calendarDays = computed(() => {

  const first = new Date(year.value, month.value, 1)

  const last = new Date(year.value, month.value + 1, 0)

  const result = []

  for (let i = 1; i <= last.getDate(); i++) {

    const date = new Date(year.value, month.value, i)

    result.push({
      key: i,
      day: i,
      date,
      isToday:
          date.toDateString() === today.toDateString()
    })
  }

  return result

})

function prevMonth() {
  current.value =
      new Date(year.value, month.value - 1, 1)
}

function nextMonth() {
  current.value =
      new Date(year.value, month.value + 1, 1)
}

</script>

<style scoped>

.calendar {
  width: 350px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  margin-bottom: 10px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.day-name {
  font-weight: bold;
  text-align: center;
}

.day {
  padding: 10px;
  text-align: center;

  cursor: pointer;
  border-radius: 6px;
}

.day:hover {
  background: var(--color-primary-soft);
}

.today {
  background: var(--color-primary);
  color: white;
}

</style>
