<script setup lang="ts">
import { ref, onMounted, onBeforeMount } from "vue"
import { useRoute, useRouter } from "vue-router"

import RecipeForm from "@/components/RecipeForm.vue"
import type { RecipeFormData, IngredientLine } from "@/components/RecipeForm.vue"
import AppContainer from "@/components/AppContainer.vue"
import AppSection from "@/components/AppSection.vue"
import AppText from "@/components/AppText.vue"
import { apiFetch, getMyUser } from "@/utilities/apiFetch"
import type { Recipe } from "@/model/Recipe"
import { showError, showSuccess } from "@/utilities/notifications"
import type { Group } from "@/model/Group"

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

const group = ref<Group | undefined>(undefined);

const myUser = getMyUser()!;
const canEditGroup = ref(false);

onBeforeMount(async () => {
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
    canEditGroup.value = data.ownerId == myUser.id;
    group.value = data.groupId ? (await apiFetch<Group>(`/api/group/${data.groupId}`)) : undefined
    if (ingredients.value.length === 0)
      ingredients.value.push({ selected: null, amount: "", unit: "" })
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

    if (canEditGroup) await apiFetch(`/api/recipes/${recipeId}/group`, "PUT", group.value?.id)

    showSuccess('Recipe updated successfully.')
    router.push("/recipes")
  } catch (error: any) {
    showError(error.message || "Could not update recipe")
  } finally {
    isSaving.value = false
  }
}
</script>

<template>
  <AppContainer class="page">
    <AppSection class="form-section">
      <AppText variant="title" tag="h1">Edit Recipe</AppText>
      <AppText v-if="isLoading">Loading recipe...</AppText>
      <RecipeForm
        v-else
        v-model="recipe"
        v-model:ingredients="ingredients"
        v-model:group="group"
        :canChangeGroup="canEditGroup"
        :is-saving="isSaving"
        submit-label="Save changes"
        @submit="submit"
        @cancel="router.push('/recipes')"
      />
    </AppSection>
  </AppContainer>
</template>

<style scoped>
.page {
  max-width: 700px;
  margin: 60px auto;
  padding: 0 24px;
}

.form-section { margin-top: 0; }
</style>
