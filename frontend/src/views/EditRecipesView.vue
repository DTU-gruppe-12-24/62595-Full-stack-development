<script setup lang="ts">
import AppButton from "@/components/AppButton.vue"
import AppInput from "@/components/AppInput.vue"
import AppText from "@/components/AppText.vue"

import type { Recipe } from "@/model/Recipe"
import { apiFetch } from "@/utilities/apiFetch"

import { ref, onMounted } from "vue"
import { useRoute, useRouter } from "vue-router"

const route = useRoute()
const router = useRouter()

const recipeId = route.params.id as string

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
    apiFetch<typeof recipe.value>(`/api/recipes/${recipeId}`)
        .then((result) => recipe.value = result)
        .catch(() => console.error("Kunne ikke finde opskrift"))
})

async function updateRecipe() {
    await apiFetch(`/api/recipes/${recipeId}`, "PUT", recipe.value)
        .then(() => router.push("/"))
        .catch(() => console.error("Kunne ikke opdatere opskrift"));
}
</script>

<template>
  <div class="page">
    <AppText variant="title" tag="h1">Rediger opskrift</AppText>

    <AppInput v-model="recipe.name" placeholder="Navn" />
    <AppInput type="textarea" v-model="recipe.description" placeholder="Beskrivelse" />
    <AppInput type="textarea" v-model="recipe.instructions" placeholder="Instruktioner" />
    <AppInput v-model="recipe.mealType" placeholder="Meal type" />
    <AppInput type="number" v-model="recipe.servings" placeholder="Portioner" />
    <AppInput type="number" v-model="recipe.prepTimeMinutes" placeholder="Tilberedningstid (min)" />

    <AppButton @click="updateRecipe">
      Gem ændringer
    </AppButton>
  </div>
</template>

<style scoped>
.page {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}
</style>