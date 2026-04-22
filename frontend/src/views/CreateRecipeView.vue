<script setup lang="ts">
import { ref } from "vue"
import { useRouter } from "vue-router"

import AppCard from "@/components/AppCard.vue"
import AppInput from "@/components/AppInput.vue"
import AppButton from "@/components/AppButton.vue"
import RecipeForm from "@/components/RecipeForm.vue"
import type { RecipeFormData, IngredientLine } from "@/components/RecipeForm.vue"
import { apiFetch } from "@/utilities/apiFetch"
import { showError } from "@/utilities/notifications"

interface ExternalIngredientDto {
  ingredientId: string | null
  ingredientName: string
  amount: number | null
  unit: string | null
}

interface ExternalRecipeDto {
  name: string
  description: string
  instructions: string
  mealType: string | null
  servings: number | null
  prepTimeMinutes: number | null
  imageUrl: string | null
  ingredients: ExternalIngredientDto[]
  source: string | null
}

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

const externalSearchQuery = ref("")
const isSearchingExternal = ref(false)
const externalSearchError = ref("")
const externalResults = ref<ExternalRecipeDto[]>([])
const selectedExternalIndex = ref<number | null>(null)

async function searchExternalRecipes() {
  externalSearchError.value = ""
  selectedExternalIndex.value = null
  const q = externalSearchQuery.value.trim()

  if (!q) {
    externalSearchError.value = "Enter a search term"
    externalResults.value = []
    return
  }

  isSearchingExternal.value = true
  try {
    const results = await apiFetch<ExternalRecipeDto[]>(
      `/api/recipes/external/search?q=${encodeURIComponent(q)}`
    )
    externalResults.value = Array.isArray(results) ? results : []
    if (externalResults.value.length === 0) {
      externalSearchError.value = "No external recipes found"
    }
  } catch (error: any) {
    externalSearchError.value = error.message || "Could not fetch external recipes"
    externalResults.value = []
  } finally {
    isSearchingExternal.value = false
  }
}

function applyExternalRecipe(selected: ExternalRecipeDto, index: number) {
  selectedExternalIndex.value = index
  errorMessage.value = ""

  recipe.value = {
    name: selected.name ?? "",
    description: selected.description ?? "",
    instructions: selected.instructions ?? "",
    mealType: selected.mealType ?? "",
    servings: selected.servings ?? null,
    prepTimeMinutes: selected.prepTimeMinutes ?? null
  }

  const mappedLines: IngredientLine[] = (selected.ingredients ?? [])
    .filter(i => i && i.ingredientName && i.ingredientName.trim())
        .map(i => ({
            selected: {
                ingredientId: i.ingredientId ?? "",
                ingredientName: i.ingredientName
            },
            amount: i.amount ?? "",
            unit: i.unit ?? ""
        })
  )

  console.log(mappedLines)

  ingredients.value = mappedLines.length > 0
    ? mappedLines
      : [{ selected: null, amount: "", unit: "" }]
  console.log(ingredients.value)
}

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

    <AppCard>
      <div class="external-search">
        <h2 class="section-title">Import from external recipe</h2>
        <p class="section-subtitle">
          Search by name, pick a result, then adjust anything before saving.
        </p>

        <div class="search-row">
          <div class="search-input">
          	<form v-on:submit.prevent="searchExternalRecipes">
              <AppInput
                :model-value="externalSearchQuery"
                label="External recipe search"
                placeholder="e.g. Arrabiata"
                @update:model-value="externalSearchQuery = String($event)"
              />
          	</form>
          </div>
          <AppButton
            variant="secondary"
            @click="searchExternalRecipes"
          >
            {{ isSearchingExternal ? "Searching..." : "Search" }}
          </AppButton>
        </div>

        <p v-if="externalSearchError" class="error">{{ externalSearchError }}</p>

        <div v-if="externalResults.length > 0" class="results">
          <button
            v-for="(result, index) in externalResults"
            :key="`${result.name}-${index}`"
            class="result-item"
            :class="{ selected: selectedExternalIndex === index }"
            type="button"
            @click="applyExternalRecipe(result, index)"
          >
            <img
              v-if="result.imageUrl"
              :src="result.imageUrl"
              :alt="result.name"
              class="thumb"
            />
            <div class="result-content">
              <p class="result-name">{{ result.name }}</p>
              <p v-if="result.source" class="result-source">{{ result.source }}</p>
              <p class="result-meta">
                {{ result.ingredients?.length ?? 0 }} ingredients
              </p>
            </div>
          </button>
        </div>
      </div>
    </AppCard>

    <RecipeForm
      v-model="recipe"
      v-model:ingredients="ingredients"
      :group="null"
      :can-change-group="true"
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
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.external-search {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.section-title {
  margin: 0;
}

.section-subtitle {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.search-row {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.search-input {
  flex: 1;
}

.results {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 360px;
  overflow: auto;
  padding-right: 2px;
}

.result-item {
  display: flex;
  align-items: center;
  gap: 10px;
  text-align: left;
  width: 100%;
  border: 1px solid #eee;
  border-radius: 10px;
  padding: 8px;
  background: #fff;
  cursor: pointer;
}

.result-item:hover {
  border-color: var(--color-primary-light);
  background: #fffaf7;
}

.result-item.selected {
  border-color: var(--color-primary);
  background: #fff7f2;
}

.thumb {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  object-fit: cover;
}

.result-content {
  min-width: 0;
}

.result-name {
  margin: 0;
  font-weight: 600;
}

.result-source,
.result-meta {
  margin: 2px 0 0;
  color: #666;
  font-size: 13px;
}

.error {
  color: #b00020;
  font-size: 14px;
  margin: 0;
}
</style>