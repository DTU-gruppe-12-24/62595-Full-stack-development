<template>
  <AppNavbar />

  <main class="main">
    <router-view />
  </main>
</template>

<script setup lang="ts">
import AppNavbar from "@/components/AppNavBar.vue"
</script>

<style>
.main {
  padding-top: 80px; /* must be larger than navbar height */
}

@media (max-width: 640px) {
  .main {
    overflow-x: hidden;
  }
}
</style>
