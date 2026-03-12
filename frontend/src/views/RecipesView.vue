<template>
  <div class="page">
    <h1>Recipes</h1>

    <button @click="openDialog">Show recipes</button>

    <div v-if="showDialog" class="modal-overlay" @click.self="closeDialog">
      <div class="modal">

        <button class="close-btn" @click="closeDialog">✕</button>
        <h2>My Recipes</h2>

        <div v-if="recipes.length === 0" class="empty-state">
          <p>No recipes yet</p>
          <router-link to="/recipes/create">Create one</router-link>
        </div>

        <div v-else class="recipe-list">
          <div v-for="recipe in recipes" :key="recipe.id" class="card">
            <h3>{{ recipe.name }}</h3>
            <p>{{ recipe.description }}</p>
            <router-link :to="`/recipes/${recipe.id}/edit`">Edit</router-link>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

interface Recipe {
  id: number
  name: string
  description: string
}

const showDialog = ref(false)
const recipes = ref<Recipe[]>([])

const openDialog = () => (showDialog.value = true)
const closeDialog = () => (showDialog.value = false)

onMounted(async () => {
  // recipes.value = await fetchRecipes()
  recipes.value = []
})
</script>

<style scoped>
.page {
  max-width: 600px;
  margin: 60px auto;
  padding: 0 24px;
  font-family: sans-serif;
  text-align: center;
}

button {
  background: #f47a2f;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
}

button:hover {
  background: #d4671e;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal {
  background: white;
  padding: 24px;
  border-radius: 10px;
  width: 400px;
  max-width: 90vw;
  position: relative;
  text-align: left;
}

.close-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  background: none;
  color: #888;
  font-size: 1rem;
  padding: 2px 6px;
}

.empty-state {
  text-align: center;
  padding: 24px 0;
  color: #888;
}

.recipe-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 12px;
}

.card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 12px;
}
</style>