<script setup lang="ts">
import { ref, onMounted } from "vue"
import { useRoute, useRouter } from "vue-router"

import AppCard from "@/components/AppCard.vue"
import AppButton from "@/components/AppButton.vue"
import AppInput from "@/components/AppInput.vue"
import { apiFetch } from "@/utilities/apiFetch"

const route = useRoute()
const router = useRouter()

const recipeId = route.params.id as string

const isLoading = ref(false)
const isSaving = ref(false)
const errorMessage = ref("")

const recipe = ref({
  name: "",
  description: "",
  instructions: "",
  mealType: "",
  servings: null as number | null,
  prepTimeMinutes: null as number | null,
  imageUrl: null as string | null,
  lastMade: null as string | null
})

onMounted(async () => {
  isLoading.value = true
  errorMessage.value = ""

  try {
    const data = await apiFetch<any>(
        `/api/recipes/${recipeId}`,
        "GET"
    )

    recipe.value = {
      name: data.name ?? "",
      description: data.description ?? "",
      instructions: data.instructions ?? "",
      mealType: data.mealType ?? "",
      servings: data.servings ?? null,
      prepTimeMinutes: data.prepTimeMinutes ?? null,
      imageUrl: data.imageUrl ?? null,
      lastMade: data.lastMade ?? null
    }
  } catch (error: any) {
    errorMessage.value = error.message || "Could not load recipe"
    console.error(error)
  } finally {
    isLoading.value = false
  }
})

async function updateRecipe() {
  errorMessage.value = ""
  isSaving.value = true

  try {
    await apiFetch(
        `/api/recipes/${recipeId}`,
        "PUT",
        recipe.value
    )

    router.push("/recipes")
  } catch (error: any) {
    errorMessage.value = error.message || "Could not update recipe"
    console.error(error)
  } finally {
    isSaving.value = false
  }
}
</script>

<template>
  <div class="page">
    <h1>Edit Recipe</h1>

    <AppCard>
      <p v-if="errorMessage" class="error">
        {{ errorMessage }}
      </p>

      <p v-if="isLoading">Loading recipe...</p>

      <template v-else>
        <AppInput
            v-model="recipe.name"
            placeholder="Recipe name"
        />

        <AppInput
            v-model="recipe.description"
            placeholder="Description"
        />

        <AppInput
            v-model="recipe.instructions"
            placeholder="Instructions"
        />

        <AppInput
            v-model="recipe.mealType"
            placeholder="Meal type"
        />

        <AppInput
            v-model="recipe.servings"
            type="number"
            placeholder="Servings"
        />

        <AppInput
            v-model="recipe.prepTimeMinutes"
            type="number"
            placeholder="Prep time (minutes)"
        />
      </template>

      <template #footer>
        <AppButton
            variant="secondary"
            @click="router.push('/recipes')"
        >
          Cancel
        </AppButton>

        <AppButton
            variant="primary"
            :disabled="isLoading || isSaving"
            @click="updateRecipe"
        >
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
  color: red;
  margin-bottom: 16px;
}
</style>