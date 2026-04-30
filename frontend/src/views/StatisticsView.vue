<script setup lang="ts">
import { ref, computed, watch } from "vue"
import { getStatistics } from "@/services/StatisticsService"
import type { StatisticsSummary } from "@/services/StatisticsService"

import AppCard from "@/components/AppCard.vue"
import AppButton from "@/components/AppButton.vue"
import AppContainer from "@/components/AppContainer.vue"
import AppSection from "@/components/AppSection.vue"
import AppText from "@/components/AppText.vue"
import GroupSelector from "@/components/GroupSelector.vue"
import WeekSelector from "@/components/WeekSelector.vue"
import type { Group } from "@/components/GroupSelector.vue"

const stats = ref<StatisticsSummary | null>(null)
const selectedGroup = ref<Group | undefined>(undefined)
const filterType = ref<'all' | 'week'>('week')

// Date Logic
const currentMonday = ref<Date>(getInitialMonday())
function getInitialMonday() {
  const now = new Date(); const day = now.getDay()
  const diff = now.getDate() - (day === 0 ? 6 : day - 1)
  const monday = new Date(now.setDate(diff)); monday.setHours(0, 0, 0, 0)
  return monday
}

const loadData = async () => {
  if (!selectedGroup.value) return
  const start = filterType.value === 'week' ? currentMonday.value.toISOString().split('T')[0] : undefined
  const end = filterType.value === 'week' ? new Date(new Date(currentMonday.value).setDate(currentMonday.value.getDate() + 6)).toISOString().split('T')[0] : undefined
  stats.value = await getStatistics(selectedGroup.value.id, start, end)
}

watch([selectedGroup, filterType, currentMonday], loadData)

const mostCooked = computed(() => {
  if (!stats.value?.general.recipeCounts) return []
  return Object.entries(stats.value.general.recipeCounts).sort((a, b) => b[1] - a[1]).slice(0, 5)
})
</script>

<template>
  <AppContainer class="statistics-page">
    <AppSection class="stats-header">
      <div class="top-row">
        <GroupSelector v-model="selectedGroup" />

        <div class="toggle-group">
          <AppButton :class="['toggle-btn', { active: filterType === 'all' }]" variant="ghost" @click="filterType = 'all'">All Time</AppButton>
          <AppButton :class="['toggle-btn', { active: filterType === 'week' }]" variant="ghost" @click="filterType = 'week'">Weekly</AppButton>
        </div>
      </div>

      <div v-if="filterType === 'week'" class="sub-row">
        <WeekSelector v-model="currentMonday" />
      </div>
    </AppSection>

    <AppText v-if="!stats" class="loading">Select a group to see statistics...</AppText>

    <AppSection v-else class="stats-section">
      <div class="stats-grid">
        <AppCard class="stat-card">
          <AppText variant="subtitle" tag="h2">Meals Planned</AppText>
          <AppText class="big-number">{{ stats.general.totalMealsPlanned }}</AppText>
        </AppCard>

        <AppCard class="stat-card">
          <AppText variant="subtitle" tag="h2">Calories</AppText>
          <AppText class="big-number">{{ Math.round(stats.nutrition.calories) }} <small>kcal</small></AppText>
        </AppCard>

        <AppCard class="stat-card">
          <AppText variant="subtitle" tag="h2">Top Recipes</AppText>
          <ul class="list">
            <li v-for="[name, count] in mostCooked" :key="name">
              <span>{{ name }}</span><strong>{{ count }}</strong>
            </li>
          </ul>
        </AppCard>

        <AppCard class="stat-card">
          <AppText variant="subtitle" tag="h2">Macros (g)</AppText>
          <ul class="list">
            <li><span>Protein</span> <strong>{{ stats.nutrition.protein.toFixed(0) }}</strong></li>
            <li><span>Carbs</span> <strong>{{ stats.nutrition.carbohydrates.toFixed(0) }}</strong></li>
            <li><span>Fat</span> <strong>{{ stats.nutrition.fat.toFixed(0) }}</strong></li>
          </ul>
        </AppCard>
      </div>
    </AppSection>
  </AppContainer>
</template>

<style scoped>
.statistics-page {
  padding: 24px;
  max-width: 95%;
  margin: 0 auto;
}

@media (max-width: 640px) {
  .statistics-page {
    padding: 16px;
    max-width: 100%;
  }
}

.stats-header {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.top-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.sub-row {
  display: flex;
  justify-content: center;
}

.toggle-group {
  display: flex;
  background: #eee;
  padding: 4px;
  border-radius: 10px;
}

.toggle-btn {
  border-radius: 8px;
  padding: 8px 20px;
  color: var(--color-text-light);
}

.toggle-btn.active {
  background: white;
  color: var(--color-primary);
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.stats-section { margin-top: 0; }

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 20px;
}

.big-number {
  font-size: 2.2rem;
  font-weight: 800;
  color: var(--color-primary);
}

.big-number small { font-size: 1rem; color: #666; }

.list { list-style: none; padding: 0; margin-top: 10px; }
.list li {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}
</style>
