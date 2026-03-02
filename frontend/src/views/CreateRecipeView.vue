<script setup lang="ts">
import { ref } from "vue"
import { useRouter } from "vue-router"

const router = useRouter()

const name = ref("")
const description = ref("")
const instructions = ref("")
const mealType = ref("")
const servings = ref<number | null>(null)
const prepTimeMinutes = ref<number | null>(null)

// Midlertidigt hardcoded UUID
const ownerId = "PUT-UUID-HER"

async function createRecipe() {
  try {
    await fetch("http://localhost:8080/api/recipes", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        owner: { id: ownerId },
        group: null,
        name: name.value,
        description: description.value,
        instructions: instructions.value,
        mealType: mealType.value,
        servings: servings.value,
        prepTimeMinutes: prepTimeMinutes.value,
        imageUrl: null,
        lastMade: null
      })
    })

    router.push("/")
  } catch (err) {
    console.error("Fejl ved oprettelse:", err)
  }
}
</script>

<template>
  <div>
    <h1>Opret opskrift</h1>

    <input v-model="name" placeholder="Navn" />
    <textarea v-model="description" placeholder="Beskrivelse"></textarea>
    <textarea v-model="instructions" placeholder="Instruktioner"></textarea>
    <input v-model="mealType" placeholder="Meal type" />
    <input type="number" v-model="servings" placeholder="Portioner" />
    <input type="number" v-model="prepTimeMinutes" placeholder="Tilberedningstid (min)" />

    <button @click="createRecipe">Opret</button>
  </div>
</template>