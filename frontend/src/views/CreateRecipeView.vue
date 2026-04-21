<script setup lang="ts">
import { ref } from "vue"
import { useRouter } from "vue-router"

import RecipeForm from "@/components/RecipeForm.vue"
import type { RecipeFormData, IngredientLine } from "@/components/RecipeForm.vue"
import { apiFetch } from "@/utilities/apiFetch"
import { showError, showSuccess } from "@/utilities/notifications"

const router = useRouter()

const isSaving = ref(false)

const recipe = ref<RecipeFormData>({
  name: "",
  description: "",
  instructions: "",
  mealType: "",
  servings: null,
  prepTimeMinutes: null
})

const ingredients = ref<IngredientLine[]>([{ selected: null, amount: "", unit: "" }])

async function submit() {

  if (!recipe.value.name.trim())
    return showError("Name is required")

  isSaving.value = true
  try {
    await apiFetch("/api/recipes", "POST", {
      ...recipe.value,
      imageUrl: null,
      lastMade: null,
      groupId: null,
      ingredients: ingredients.value
        .filter(l => l.selected)
        .map(l => ({
          ingredientName: l.selected!.ingredientName,
          amount: l.amount === "" ? null : Number(l.amount),
          unit: l.unit.trim() || null
        }))
    })
    showSuccess('Recipe created successfully.')
    router.push("/recipes")
  } catch (error: any) {
    showError(error.message || "Could not create recipe")
  } finally {
    isSaving.value = false
  }
}
</script>

<template>
  <div class="page">
    <h1>Create Recipe</h1>
    <RecipeForm
      v-model="recipe"
      v-model:ingredients="ingredients"
      :is-saving="isSaving"
      submit-label="Create Recipe"
      @submit="submit"
      @cancel="router.push('/recipes')"
    />
  </div>
</template>

<style scoped>
.page {
  max-width: 700px;
  margin: 60px auto;
  padding: 0 24px;
}
</style>
