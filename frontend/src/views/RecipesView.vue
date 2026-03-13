<script setup lang="ts">
import { ref, onMounted } from "vue"

import AppCard from "@/components/AppCard.vue"
import AppButton from "@/components/AppButton.vue"
import AppInput from "@/components/AppInput.vue"
import AppDialog from "@/components/AppDialog.vue"
import { apiFetch } from "@/utilities/apiFetch"

const recipe = ref<Recipe>({
  name: "",
  description: "",
  instructions: "",
  mealType: "",
  servings: null,
  prepTimeMinutes: null,
  imageUrl: null,
  lastMade: null
})

const recipes = ref<Recipe[]>([])
const selectedRecipe = ref<Recipe | null>(null)

const isLoading = ref(false)
const isCreating = ref(false)
const errorMessage = ref("")

const showRecipeDialog = ref(false)
const showCreateDialog = ref(false)

const newRecipe = ref({
  name: "",
  description: "",
  instructions: "",
  mealType: "",
  servings: null as number | null,
  prepTimeMinutes: null as number | null
})

onMounted(async () => {
  await loadRecipes()
})

async function loadRecipes() {
  isLoading.value = true
  errorMessage.value = ""

  try {
    recipes.value = await apiFetch("/api/recipes", "GET")
  } catch (error: any) {
    errorMessage.value = error.message || "Could not load recipes"
    console.error(error)
  } finally {
    isLoading.value = false
  }
}

function openRecipe(recipe: Recipe) {
  selectedRecipe.value = recipe
  showRecipeDialog.value = true
}

function resetCreateForm() {
  newRecipe.value = {
    name: "",
    description: "",
    instructions: "",
    mealType: "",
    servings: null,
    prepTimeMinutes: null
  }
}

async function createRecipe() {
  errorMessage.value = ""

  if (!newRecipe.value.name.trim()) {
    errorMessage.value = "Name is required"
    return
  }

  isCreating.value = true

  try {
    await apiFetch("/api/recipes", "POST", {
      name: newRecipe.value.name,
      description: newRecipe.value.description,
      instructions: newRecipe.value.instructions,
      mealType: newRecipe.value.mealType,
      servings: newRecipe.value.servings,
      prepTimeMinutes: newRecipe.value.prepTimeMinutes,
      imageUrl: null,
      lastMade: null,
      groupId: null
    })

    showCreateDialog.value = false
    resetCreateForm()
    await loadRecipes()
  } catch (error: any) {
    errorMessage.value = error.message || "Could not create recipe"
    console.error(error)
  } finally {
    isCreating.value = false
  }
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h1>Recipes</h1>
        <p class="subtitle">Browse recipes and open details in a dialog.</p>
      </div>

      <AppButton variant="primary" @click="showCreateDialog = true">
        Create Recipe
      </AppButton>
    </div>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    <p v-if="isLoading">Loading recipes...</p>

    <div v-else class="recipe-grid">
      <AppCard
          v-for="recipe in recipes"
          :key="recipe.id"
          hover
          class="recipe-card"
          @click="openRecipe(recipe)"
      >
        <h3>{{ recipe.name }}</h3>
        <p>{{ recipe.description || "No description yet." }}</p>

        <template #footer>
          <span class="meta">
            {{ recipe.mealType || "Unknown type" }}
          </span>
        </template>
      </AppCard>
    </div>

    <AppDialog
        v-model="showRecipeDialog"
        :title="selectedRecipe?.name || 'Recipe details'"
    >
      <template v-if="selectedRecipe">
        <div class="detail-list">
          <p><strong>Description:</strong> {{ selectedRecipe.description || "—" }}</p>
          <p><strong>Instructions:</strong> {{ selectedRecipe.instructions || "—" }}</p>
          <p><strong>Meal type:</strong> {{ selectedRecipe.mealType || "—" }}</p>
          <p><strong>Servings:</strong> {{ selectedRecipe.servings ?? "—" }}</p>
          <p><strong>Prep time:</strong> {{ selectedRecipe.prepTimeMinutes ?? "—" }} min</p>
          <p><strong>Last made:</strong> {{ selectedRecipe.lastMade || "—" }}</p>
        </div>
      </template>

      <template #footer>
        <AppButton variant="secondary" @click="showRecipeDialog = false">
          Close
        </AppButton>
      </template>
    </AppDialog>

    <AppDialog
        v-model="showCreateDialog"
        title="Create Recipe"
    >
      <AppInput
          v-model="newRecipe.name"
          placeholder="Recipe name"
      />

      <AppInput
          v-model="newRecipe.description"
          placeholder="Description"
      />

      <AppInput
          v-model="newRecipe.instructions"
          placeholder="Instructions"
      />

      <AppInput
          v-model="newRecipe.mealType"
          placeholder="Meal type"
      />

      <AppInput
          v-model="newRecipe.servings"
          type="number"
          placeholder="Servings"
      />

      <AppInput
          v-model="newRecipe.prepTimeMinutes"
          type="number"
          placeholder="Prep time (minutes)"
      />

      <template #footer>
        <AppButton
            variant="secondary"
            @click="showCreateDialog = false"
        >
          Cancel
        </AppButton>

        <AppButton
            variant="primary"
            :disabled="isCreating"
            @click="createRecipe"
        >
          {{ isCreating ? "Creating..." : "Create" }}
        </AppButton>
      </template>
    </AppDialog>
  </div>
</template>

<style scoped>
.page {
  padding: 40px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 24px;
}

.subtitle {
  margin-top: 8px;
  color: #666;
}

.recipe-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}

.recipe-card {
  cursor: pointer;
}

.meta {
  font-size: 0.9rem;
  color: #666;
}

.detail-list {
  display: grid;
  gap: 10px;
}

.error {
  color: red;
  margin-bottom: 16px;
}
</style>