<script setup lang="ts">
import { ref, onMounted } from "vue"

const recipes = ref<any[]>([])

onMounted(async () => {
  const response = await fetch("http://localhost:8080/api/recipes")
  const data = await response.json()
  recipes.value = data
})
</script>

<template>
  <div class="page">
    <h1>Opskrifter</h1>

    <div v-if="recipes.length === 0">
      Ingen opskrifter endnu.
    </div>

    <div v-for="recipe in recipes" :key="recipe.id" class="card">
      <h2>{{ recipe.name }}</h2>
      <p>{{ recipe.description }}</p>

      <router-link :to="`/recipes/${recipe.id}/edit`">
        Rediger
      </router-link>
    </div>
  </div>
</template>

<style scoped>
.page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
.card {
  border: 1px solid #ddd;
  padding: 16px;
  margin-bottom: 16px;
}
</style>