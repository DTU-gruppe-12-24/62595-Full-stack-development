<script setup lang="ts">
import { ref } from "vue"
import { useRouter } from "vue-router"

import AppCard from "@/components/AppCard.vue"
import AppButton from "@/components/AppButton.vue"
import AppInput from "@/components/AppInput.vue"

import { apiFetch } from "@/utilities/apiFetch.ts";

const router = useRouter()

const isLoading = ref(false)
const errorMessage = ref("")

const recipe = ref({
  name: "",
  description: "",
  instructions: "",
  mealType: "",
  servings: null as number | null,
  prepTimeMinutes: null as number | null
})

async function createRecipe() {
  errorMessage.value = ""

  if (!recipe.value.name.trim()) {
    errorMessage.value = "Name is required"
    return
  }

  isLoading.value = true

  try {
    await apiFetch("/api/recipes", "POST", {
      name: recipe.value.name,
      description: recipe.value.description,
      instructions: recipe.value.instructions,
      mealType: recipe.value.mealType,
      servings: recipe.value.servings,
      prepTimeMinutes: recipe.value.prepTimeMinutes,
      imageUrl: null,
      lastMade: null,
      groupId: null
    })

    router.push("/recipes")

  } catch (error: any) {
    errorMessage.value = error.message || "Something went wrong"
    console.error(error)

  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="page">

    <h1>Create Recipe</h1>

    <AppCard>

      <p v-if="errorMessage" class="error">
        {{ errorMessage }}
      </p>

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

      <template #footer>

        <AppButton
            variant="secondary"
            @click="router.push('/recipes')"
        >
          Cancel
        </AppButton>

        <AppButton
            variant="primary"
            :disabled="isLoading"
            @click="createRecipe"
        >
          {{ isLoading ? "Creating..." : "Create Recipe" }}
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