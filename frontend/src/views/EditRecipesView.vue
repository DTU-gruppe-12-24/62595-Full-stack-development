<script setup lang="ts">
import { ref, onMounted } from "vue"
import { useRoute, useRouter } from "vue-router"

const route = useRoute()
const router = useRouter()

const recipeId = route.params.id as string

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
  const response = await fetch(`http://localhost:8080/api/recipes/${recipeId}`)
  const data = await response.json()
  recipe.value = data
})

async function updateRecipe() {
  const response = await fetch(
      `http://localhost:8080/api/recipes/${recipeId}`,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(recipe.value)
      }
  )

  if (!response.ok) {
    console.error("Kunne ikke opdatere opskrift")
    return
  }

  router.push("/")
}
</script>

<template>
  <div class="page">
    <h1>Rediger opskrift</h1>

    <input v-model="recipe.name" placeholder="Navn" />
    <textarea v-model="recipe.description" placeholder="Beskrivelse"></textarea>
    <textarea v-model="recipe.instructions" placeholder="Instruktioner"></textarea>
    <input v-model="recipe.mealType" placeholder="Meal type" />
    <input type="number" v-model="recipe.servings" placeholder="Portioner" />
    <input type="number" v-model="recipe.prepTimeMinutes" placeholder="Tilberedningstid (min)" />

    <button @click="updateRecipe">
      Gem ændringer
    </button>
  </div>
</template>

<style scoped>
.page {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}
input,
textarea {
  display: block;
  width: 100%;
  margin-bottom: 12px;
  padding: 8px;
}
button {
  padding: 10px 16px;
  cursor: pointer;
}
</style>