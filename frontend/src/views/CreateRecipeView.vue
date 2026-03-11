<script setup lang="ts">
import AppButton from "@/components/AppButton.vue"
import AppInput from "@/components/AppInput.vue"
import AppText from "@/components/AppText.vue"
import { apiFetch } from "@/utilities/apiFetch"
import { ref } from "vue"
import { useRouter } from "vue-router"

const router = useRouter()

const ownerId = "PUT-UUID-HERE"

const recipe = ref({
  name: "",
  description: "",
  instructions: "",
  mealType: "",
  servings: null as number | null,
  prepTimeMinutes: null as number | null
})

async function createRecipe() {
    await apiFetch("/api/recipes", "POST", {
        group: null,
        name: recipe.value.name,
        description: recipe.value.description,
        instructions: recipe.value.instructions,
        mealType: recipe.value.mealType,
        servings: recipe.value.servings,
        prepTimeMinutes: recipe.value.prepTimeMinutes,
        imageUrl: null,
        lastMade: null
    })
        .then(() => router.push("/"))
        .catch(() => console.error("Kunne ikke oprette opskrift"))
}
</script>

<template>
  <div class="page">
    <AppText variant="title" tag="h1">Opret opskrift</AppText>

    <AppInput v-model="recipe.name" placeholder="Navn" />
    <AppInput type="textarea" v-model="recipe.description" placeholder="Beskrivelse" />
    <AppInput type="textarea" v-model="recipe.instructions" placeholder="Instruktioner" />
    <AppInput v-model="recipe.mealType" placeholder="Meal type" />
    <AppInput type="number" min="0" v-model="recipe.servings" placeholder="Portioner" />
    <AppInput type="number" min="0" v-model="recipe.prepTimeMinutes" placeholder="Tilberedningstid (min)" />

    <AppButton variant="primary" @click="createRecipe">Opret</AppButton>
  </div>
</template>

<style scoped>
.page {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}
</style>