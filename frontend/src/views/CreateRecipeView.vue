<script setup lang="ts">
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
  try {
    const response = await fetch("http://localhost:8080/api/recipes", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        owner: { id: ownerId },
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
    })

    if (!response.ok) {
      throw new Error("Couldn't create recipe")
    }

    router.push("/")
  } catch (error) {
    console.error("Error:", error)
  }
}
</script>

<template>
  <div>
    <h1>Create recipe</h1>

    <input v-model="recipe.name" placeholder="Name" />

    <textarea
        v-model="recipe.description"
        placeholder="Description"
    ></textarea>

    <textarea
        v-model="recipe.instructions"
        placeholder="Instructions"
    ></textarea>

    <input v-model="recipe.mealType" placeholder="Meal type" />

    <input
        type="number"
        v-model="recipe.servings"
        placeholder="Portions"
    />

    <input
        type="number"
        v-model="recipe.prepTimeMinutes"
        placeholder="Preperation time (min)"
    />

    <button @click="createRecipe">
      Create
    </button>
  </div>
</template>