<script setup lang="ts">
import { computed } from 'vue'
import AppButton from '@/components/AppButton.vue'
import AppText from '@/components/AppText.vue'

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
    <AppButton variant="ghost" class="nav-btn" @click="changeWeek(-1)">←</AppButton>

    <div class="date-info" @click="resetToToday">
      <AppText v-if="isCurrentWeek" variant="caption" class="current-badge">Current Week</AppText>
      <AppText class="range">{{ dateRangeLabel }}</AppText>
    </div>

    <AppButton variant="ghost" class="nav-btn" @click="changeWeek(1)">→</AppButton>
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

@media (max-width: 400px) {
  .date-info {
    min-width: 120px;
  }

  .range {
    font-size: 0.78rem;
  }
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
  font-size: 1.2rem;
  color: var(--color-primary);
  padding: 0 10px;
  transition: transform 0.2s;
}

.nav-btn:hover {
  transform: scale(1.2);
}
</style>
