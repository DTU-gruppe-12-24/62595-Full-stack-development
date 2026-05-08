<script setup lang="ts">
import { computed } from 'vue'

import AppContainer from '@/components/AppContainer.vue'
import AppSection from '@/components/AppSection.vue'
import AppCard from '@/components/AppCard.vue'
import AppText from '@/components/AppText.vue'
import AppButton from '@/components/AppButton.vue'
import { getStoredUser, isAuthenticated } from '@/services/authService'

const authenticated = computed(() => isAuthenticated())
const userName = computed(() => getStoredUser()?.name ?? 'there')
</script>

<template>
  <AppContainer class="home-page">
    <AppSection class="hero">
      <template v-if="!authenticated">
        <AppText variant="title" tag="h1">OurMenu makes weekly food planning simple</AppText>
        <AppText class="hero-subtitle">
          Plan meals, manage recipes, build shared shopping lists, and keep your food routine organized in one place.
        </AppText>
        <div class="hero-actions">
          <AppButton variant="primary" to="/sign-up">Get Started</AppButton>
          <AppButton variant="secondary" to="/sign-in">Sign In</AppButton>
        </div>
      </template>

      <template v-else>
        <AppText variant="title" tag="h1">Welcome back, {{ userName }}</AppText>
        <AppText class="hero-subtitle">
          Pick up where you left off. Jump straight into your recipes, shopping list, and planning tools.
        </AppText>
        <div class="hero-actions">
          <AppButton variant="primary" to="/recipes">Open Recipes</AppButton>
          <AppButton variant="secondary" to="/shopping">Open Shopping List</AppButton>
        </div>
      </template>
    </AppSection>

    <AppSection>
      <template v-if="!authenticated">
        <div class="feature-grid">
          <AppCard>
            <AppText variant="subtitle" tag="h2">Smart Meal Planning</AppText>
            <AppText>Use your weekly calendar to plan meals and keep everyone on track.</AppText>
          </AppCard>

          <AppCard>
            <AppText variant="subtitle" tag="h2">Shared Shopping Lists</AppText>
            <AppText>Turn plans into shopping lists your household can update together.</AppText>
          </AppCard>

          <AppCard>
            <AppText variant="subtitle" tag="h2">Groups and Collaboration</AppText>
            <AppText>Create groups for family or roommates and manage food planning as a team.</AppText>
          </AppCard>
        </div>
      </template>

      <template v-else>
        <div class="feature-grid">
          <AppCard>
            <AppText variant="subtitle" tag="h2">Plan Your Week</AppText>
            <AppText>Organize breakfast, lunch, and dinner with your weekly calendar.</AppText>
            <template #footer>
              <AppButton variant="secondary" to="/calendar">Go to Calendar</AppButton>
            </template>
          </AppCard>

          <AppCard>
            <AppText variant="subtitle" tag="h2">Manage Your Groups</AppText>
            <AppText>Invite members, manage roles, and keep planning shared and clear.</AppText>
            <template #footer>
              <AppButton variant="secondary" to="/groups">Go to Groups</AppButton>
            </template>
          </AppCard>

          <AppCard>
            <AppText variant="subtitle" tag="h2">Track Your Progress</AppText>
            <AppText>Review your weekly and all-time nutrition insights.</AppText>
            <template #footer>
              <AppButton variant="secondary" to="/nutrition">Go to Nutrition</AppButton>
            </template>
          </AppCard>
        </div>
      </template>
    </AppSection>
  </AppContainer>
</template>

<style scoped>
.home-page {
  padding-block: 32px 48px;
}

.hero {
  margin-top: 0;
  padding: 32px;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, #fff3e8 0%, #fff9ef 45%, #f4fbf4 100%);
  border: 1px solid #f9c74f35;
}

.hero-subtitle {
  margin-top: 8px;
  max-width: 680px;
  color: var(--color-text-light);
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 20px;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

@media (max-width: 640px) {
  .home-page {
    padding-block: 20px 32px;
  }

  .hero {
    padding: 20px;
  }
}
</style>
