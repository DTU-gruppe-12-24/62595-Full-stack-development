<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  modelValue: Date
}>()

const emit = defineEmits<{
  'update:modelValue': [value: Date]
}>()

const isCurrentWeek = computed(() => {
  const today = new Date()
  const day = today.getDay()
  const diff = today.getDate() - (day === 0 ? 6 : day - 1)
  const realMonday = new Date(today.setDate(diff))
  realMonday.setHours(0, 0, 0, 0)

  return props.modelValue.getTime() === realMonday.getTime()
})

const dateRangeLabel = computed(() => {
  const start = new Date(props.modelValue)
  const end = new Date(props.modelValue)
  end.setDate(start.getDate() + 6)
  return `${start.toLocaleDateString()} - ${end.toLocaleDateString()}`
})

function changeWeek(weeks: number) {
  const newDate = new Date(props.modelValue)
  newDate.setDate(newDate.getDate() + (weeks * 7))
  emit('update:modelValue', newDate)
}

function resetToToday() {
  const now = new Date()
  const day = now.getDay()
  const diff = now.getDate() - (day === 0 ? 6 : day - 1)
  const monday = new Date(now.setDate(diff))
  monday.setHours(0, 0, 0, 0)
  emit('update:modelValue', monday)
}
</script>

<template>
  <div class="week-selector" :class="{ 'is-current': isCurrentWeek }">
    <button class="nav-btn" @click="changeWeek(-1)">←</button>

    <div class="date-info" @click="resetToToday">
      <span v-if="isCurrentWeek" class="current-badge">Current Week</span>
      <span class="range">{{ dateRangeLabel }}</span>
    </div>

    <button class="nav-btn" @click="changeWeek(1)">→</button>
  </div>
</template>

<style scoped>
.week-selector {
  display: flex;
  align-items: center;
  gap: 12px;
  background: white;
  padding: 8px 16px;
  border-radius: 25px;
  border: 1px solid var(--color-border, #ddd);
  transition: all 0.3s ease;
}

.week-selector.is-current {
  border-color: var(--color-primary);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.date-info {
  display: flex;
  flex-direction: column;
  cursor: pointer;
  text-align: center;
  min-width: 180px;
}

.current-badge {
  font-size: 0.65rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--color-primary);
  font-weight: 800;
  margin-bottom: -2px;
}

.range {
  font-weight: 600;
  font-size: 0.9rem;
}

.nav-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  color: var(--color-primary);
  cursor: pointer;
  padding: 0 10px;
  transition: transform 0.2s;
}

.nav-btn:hover {
  transform: scale(1.2);
}
</style>