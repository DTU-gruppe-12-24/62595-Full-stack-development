<script setup lang="ts">
import { ref, onMounted } from "vue"
import { useRoute, useRouter } from "vue-router"

import RecipeForm from "@/components/RecipeForm.vue"
import type { RecipeFormData, IngredientLine } from "@/components/RecipeForm.vue"
import { apiFetch } from "@/utilities/apiFetch"
import type { Recipe } from "@/model/Recipe"
import { showError } from "@/utilities/notifications"

const route = useRoute()
const router = useRouter()

const recipeId = route.params.id as string

const isLoading = ref(false)
const isSaving = ref(false)

const recipe = ref<RecipeFormData>({
  name: "",
  description: "",
  instructions: "",
  mealType: "",
  servings: null,
  prepTimeMinutes: null
})

const ingredients = ref<IngredientLine[]>([])

onMounted(async () => {
  isLoading.value = true
  try {
    const data = await apiFetch<Recipe>(`/api/recipes/${recipeId}`, "GET")
    recipe.value = {
      name: data.name ?? "",
      description: data.description ?? "",
      instructions: data.instructions ?? "",
      mealType: data.mealType ?? "",
      servings: data.servings ?? null,
      prepTimeMinutes: data.prepTimeMinutes ?? null
    }
    ingredients.value = (data.ingredients ?? []).map(ing => ({
      selected: { ingredientId: ing.ingredientId, ingredientName: ing.ingredientName },
      amount: ing.amount ?? "",
      unit: ing.unit ?? ""
    }))
    if (ingredients.value.length === 0) {
      ingredients.value.push({ selected: null, amount: "", unit: "" })
    }
  } catch (error: any) {
    showError(error.message || "Could not load recipe")
  } finally {
    isLoading.value = false
  }
})

async function submit() {
  isSaving.value = true
  try {
    await apiFetch(`/api/recipes/${recipeId}`, "PUT", {
      ...recipe.value,
      ingredients: ingredients.value
        .filter(l => l.selected)
        .map(l => ({
          ingredientName: l.selected!.ingredientName,
          amount: l.amount === "" ? null : Number(l.amount),
          unit: l.unit.trim() || null
        }))
    })
    router.push("/recipes")
  } catch (error: any) {
    showError(error.message || "Could not update recipe")
  } finally {
    isSaving.value = false
  }
}
</script>

<template>
  <div class="page">
    <h1>Edit Recipe</h1>
    <p v-if="isLoading">Loading recipe...</p>
    <RecipeForm
      v-else
      v-model="recipe"
      v-model:ingredients="ingredients"
      :is-saving="isSaving"
      submit-label="Save changes"
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
