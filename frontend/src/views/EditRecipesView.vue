<script setup lang="ts">
import { ref, onMounted } from "vue"
import { useRoute, useRouter } from "vue-router"

import AppCard from "@/components/AppCard.vue"
import AppButton from "@/components/AppButton.vue"
import AppInput from "@/components/AppInput.vue"
import { apiFetch } from "@/utilities/apiFetch"
import type { Recipe } from "@/model/Recipe"

const route = useRoute()
const router = useRouter()

const recipeId = route.params.id as string

const isLoading = ref(false)
const isSaving = ref(false)
const errorMessage = ref("")

const recipe = ref<Partial<Recipe>>({
  name: "",
  description: "",
  instructions: "",
  mealType: "",
  servings: undefined,
  prepTimeMinutes: undefined,
  imageUrl: undefined,
  lastMade: undefined
})

onMounted(async () => {
  isLoading.value = true
  errorMessage.value = ""

  try {
    const data = await apiFetch<Recipe>(`/api/recipes/${recipeId}`, "GET")

    recipe.value = {
      name: data.name ?? "",
      description: data.description ?? "",
      instructions: data.instructions ?? "",
      mealType: data.mealType ?? "",
      servings: data.servings ?? undefined,
      prepTimeMinutes: data.prepTimeMinutes ?? undefined,
      imageUrl: data.imageUrl ?? undefined,
      lastMade: data.lastMade ?? undefined
    }
  } catch (error: any) {
    errorMessage.value = error.message || "Could not load recipe"
  } finally {
    isLoading.value = false
  }
})

async function updateRecipe() {
  errorMessage.value = ""
  isSaving.value = true

  try {
    await apiFetch(`/api/recipes/${recipeId}`, "PUT", recipe.value)
    router.push("/recipes")
  } catch (error: any) {
    errorMessage.value = error.message || "Could not update recipe"
  } finally {
    isSaving.value = false
  }
}
</script>

<template>
  <div class="page">
    <h1>Edit Recipe</h1>

    <AppCard>
      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
      <p v-if="isLoading">Loading recipe...</p>

      <template v-else>
        <AppInput v-model="recipe.name" label="Name" placeholder="Recipe name" />
        <AppInput v-model="recipe.description" label="Description" placeholder="Description" />
        <AppInput v-model="recipe.instructions" label="Instructions" placeholder="Instructions" />
        <AppInput v-model="recipe.mealType" label="Meal type" placeholder="e.g. Dinner" />
        <AppInput v-model="recipe.servings" label="Servings" type="number" placeholder="e.g. 4" />
        <AppInput v-model="recipe.prepTimeMinutes" label="Prep time (minutes)" type="number" placeholder="e.g. 30" />
      </template>

      <template #footer>
        <AppButton variant="secondary" @click="router.push('/recipes')">Cancel</AppButton>
        <AppButton variant="primary" :disabled="isLoading || isSaving" @click="updateRecipe">
          {{ isSaving ? "Saving..." : "Save changes" }}
        </AppButton>
      </template>
    </AppCard>
  </div>
</template>

<style scoped>
.page {
  max-width: 700px;
  margin: 60px auto;
  padding: 0 24px;
}

.error {
  color: #c0392b;
  margin-bottom: 16px;
}
</style>
