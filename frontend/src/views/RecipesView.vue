<script setup lang="ts">
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"

import AppCard from "@/components/AppCard.vue"
import AppButton from "@/components/AppButton.vue"
import AppDialog from "@/components/AppDialog.vue"
import { apiFetch } from "@/utilities/apiFetch"
import { getStoredUser } from "@/services/authService"
import type { Recipe } from "@/model/Recipe"

const router = useRouter()
const currentUserId = getStoredUser()?.userId

const recipes = ref<Recipe[]>([])
const selectedRecipe = ref<Recipe | null>(null)
const isLoading = ref(false)
const errorMessage = ref("")
const showRecipeDialog = ref(false)

onMounted(async () => {
  await loadRecipes()
})

async function loadRecipes() {
  isLoading.value = true
  errorMessage.value = ""
  try {
    recipes.value = await apiFetch<Recipe[]>("/api/recipes", "GET")
  } catch (error: any) {
    errorMessage.value = error.message || "Could not load recipes"
  } finally {
    isLoading.value = false
  }
}

function openRecipe(recipe: Recipe) {
  selectedRecipe.value = recipe
  showRecipeDialog.value = true
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h1>Recipes</h1>
        <p class="subtitle">Browse recipes and open details in a dialog.</p>
      </div>
      <AppButton variant="primary" @click="router.push('/recipes/create')">Create Recipe</AppButton>
    </div>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    <p v-if="isLoading">Loading recipes...</p>

    <div v-else class="recipe-grid">
      <AppCard
        v-for="recipe in recipes"
        :key="recipe.id"
        class="recipe-card"
        @click="openRecipe(recipe)"
      >
        <h3>{{ recipe.name }}</h3>
        <p>{{ recipe.description || "No description yet." }}</p>
        <template #footer>
          <span class="meta">{{ recipe.mealType || "Unknown type" }}</span>
        </template>
      </AppCard>
    </div>

    <AppDialog v-model="showRecipeDialog" :title="selectedRecipe?.name || 'Recipe details'" width="560px">
      <template v-if="selectedRecipe">
        <div class="detail-list">
          <p><strong>Description:</strong> {{ selectedRecipe.description || "-" }}</p>
          <p><strong>Instructions:</strong> {{ selectedRecipe.instructions || "-" }}</p>
          <p><strong>Meal type:</strong> {{ selectedRecipe.mealType || "-" }}</p>
          <p><strong>Servings:</strong> {{ selectedRecipe.servings ?? "-" }}</p>
          <p><strong>Prep time:</strong> {{ selectedRecipe.prepTimeMinutes ?? "-" }} min</p>
          <p><strong>Last made:</strong> {{ selectedRecipe.lastMade || "-" }}</p>
          <p><strong>Created by:</strong> {{ selectedRecipe.ownerName }}</p>
          <div v-if="selectedRecipe.ingredients?.length" class="ingredients-section">
            <p><strong>Ingredients:</strong></p>
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
        <AppButton variant="secondary" @click="showRecipeDialog = false">Close</AppButton>
        <AppButton
          v-if="selectedRecipe?.ownerId === currentUserId"
          variant="primary"
          @click="router.push(`/recipes/${selectedRecipe!.id}/edit`)"
        >
          Edit
        </AppButton>
      </template>
    </AppDialog>
  </div>
</template>

<style scoped>
.page { padding: 40px; }

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

.error { color: #c0392b; margin: 0; }
</style>
