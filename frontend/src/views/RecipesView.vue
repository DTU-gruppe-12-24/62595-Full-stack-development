<template>
  <div class="page">
    <h1>Recipes</h1>

    <button @click="openDialog">
      Show recipes
    </button>

    <div v-if="showDialog" class="modal-overlay">
      <div class="modal">

        <button class="close" @click="closeDialog">
          Close
        </button>

        <h2>Recipes</h2>

        <div v-if="recipes.length === 0">
          No recipes yet
        </div>

        <div class="recipe-list">
          <div v-for="recipe in recipes" :key="recipe.id" class="card">
            <h2>{{ recipe.name }}</h2>
            <p>{{ recipe.description }}</p>

            <router-link :to="`/recipes/${recipe.id}/edit`">
              Edit
            </router-link>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>