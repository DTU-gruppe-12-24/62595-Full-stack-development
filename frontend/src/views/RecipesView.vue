<script setup lang="ts">
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"

import AppCard from "@/components/AppCard.vue"
import AppButton from "@/components/AppButton.vue"
import AppDialog from "@/components/AppDialog.vue"
import AppConfirmDialog from "@/components/AppConfirmDialog.vue"
import AppContainer from "@/components/AppContainer.vue"
import AppSection from "@/components/AppSection.vue"
import AppText from "@/components/AppText.vue"
import { apiFetch } from "@/utilities/apiFetch"
import { getStoredUser } from "@/services/authService"
import type { Recipe } from "@/model/Recipe"
import { showError, showSuccess } from "@/utilities/notifications"

const router = useRouter()
const currentUserId = getStoredUser()?.userId

const recipes = ref<Recipe[]>([])
const selectedRecipe = ref<Recipe | null>(null)
const isLoading = ref(false)
const showRecipeDialog = ref(false)
const showDeleteConfirm = ref(false)

onMounted(async () => {
  await loadRecipes()
})

async function loadRecipes() {
  isLoading.value = true
  try {
    recipes.value = await apiFetch<Recipe[]>("/api/recipes", "GET")
  } catch (error: any) {
    showError(error.message || "Could not load recipes")
  } finally {
    isLoading.value = false
  }
}

function openRecipe(recipe: Recipe) {
  selectedRecipe.value = recipe
  showRecipeDialog.value = true
}

function requestDeleteRecipe() {
  if (!selectedRecipe.value) return
  showDeleteConfirm.value = true
}

async function confirmDeleteRecipe() {
  if (!selectedRecipe.value) return
  const recipe = selectedRecipe.value

  try {
    await apiFetch(`/api/recipes/${recipe.id}`, "DELETE")
    recipes.value = recipes.value.filter(r => r.id !== recipe.id)
    showDeleteConfirm.value = false
    showRecipeDialog.value = false
    selectedRecipe.value = null
    showSuccess("Recipe deleted successfully.")
  } catch (error: any) {
    showError(error.message || "Could not delete recipe")
  }
}
</script>

<template>
  <AppContainer class="page">
    <AppSection class="header-section">
      <div class="page-header">
        <div>
          <AppText variant="title" tag="h1">Recipes</AppText>
          <AppText variant="caption" class="subtitle">Browse recipes and open details in a dialog.</AppText>
        </div>
        <AppButton variant="primary" to="/recipes/create">Create Recipe</AppButton>
      </div>
    </AppSection>

    <AppText v-if="isLoading">Loading recipes...</AppText>

    <AppSection v-else>
      <div class="recipe-grid">
        <AppCard
          v-for="recipe in recipes"
          :key="recipe.id"
          class="recipe-card"
          @click="openRecipe(recipe)"
        >
          <AppText variant="subtitle" tag="h3">{{ recipe.name }}</AppText>
          <AppText>{{ recipe.description || "No description yet." }}</AppText>
          <template #footer>
            <AppText variant="caption" class="meta">{{ recipe.mealType || "Unknown type" }}</AppText>
          </template>
        </AppCard>
      </div>
    </AppSection>

    <AppDialog v-model="showRecipeDialog" :title="selectedRecipe?.name || 'Recipe details'" width="560px">
      <template v-if="selectedRecipe">
        <div class="detail-list">
          <AppText><strong>Description:</strong> {{ selectedRecipe.description || "-" }}</AppText>
          <AppText><strong>Instructions:</strong> {{ selectedRecipe.instructions || "-" }}</AppText>
          <AppText><strong>Meal type:</strong> {{ selectedRecipe.mealType || "-" }}</AppText>
          <AppText><strong>Servings:</strong> {{ selectedRecipe.servings ?? "-" }}</AppText>
          <AppText><strong>Prep time:</strong> {{ selectedRecipe.prepTimeMinutes ?? "-" }} min</AppText>
          <AppText><strong>Last made:</strong> {{ selectedRecipe.lastMade || "-" }}</AppText>
          <AppText><strong>Created by:</strong> {{ selectedRecipe.ownerName }}</AppText>
          <AppText v-if="selectedRecipe.groupId"><strong>Part of group:</strong> {{ selectedRecipe.groupName }}</AppText>
          <div v-if="selectedRecipe.ingredients?.length" class="ingredients-section">
            <AppText><strong>Ingredients:</strong></AppText>
            <ul class="ingredient-list">
              <li v-for="ing in selectedRecipe.ingredients" :key="ing.ingredientId">
                - {{ ing.ingredientName }}
                <span v-if="ing.amount"> - {{ ing.amount }}{{ ing.unit ? ' ' + ing.unit : '' }}</span>
              </li>
            </ul>
          </div>
        </div>
      </template>

      <template #footer>
        <AppButton variant="cancel" @click="showRecipeDialog = false">Cancel</AppButton>
        <AppButton
          v-if="selectedRecipe?.ownerId === currentUserId"
          variant="danger"
          @click="requestDeleteRecipe"
        >
          Delete
        </AppButton>
        <AppButton
          v-if="selectedRecipe?.ownerId === currentUserId"
          variant="primary"
          :to="`/recipes/${selectedRecipe!.id}/edit`"
        >
          Edit
        </AppButton>
      </template>
    </AppDialog>

    <AppConfirmDialog
      v-model="showDeleteConfirm"
      :title="`Delete recipe '${selectedRecipe?.name ?? ''}'?`"
      message="Deleting a recipe cannot be undone."
      confirm-label="Delete recipe"
      confirm-variant="danger"
      @confirm="confirmDeleteRecipe"
    />
  </AppContainer>
</template>

<style scoped>
.page { padding: 40px; }

@media (max-width: 640px) {
  .page { padding: 16px; }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
}

.header-section { margin-top: 0; }

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 24px;
}

.subtitle { margin-top: 8px; color: #666; }

.recipe-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}

.recipe-card { cursor: pointer; }

.meta { font-size: 0.9rem; color: #666; }

.detail-list { display: grid; gap: 10px; }

.ingredients-section { margin-top: 16px; }
.ingredients-section h4 { margin: 0 0 8px; font-size: 14px; font-weight: 600; }

.ingredient-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 14px;
}
</style>
