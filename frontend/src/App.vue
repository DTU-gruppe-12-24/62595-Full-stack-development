<template>
  <AppNavbar />

  <main class="main">
    <router-view />
  </main>
</template>

<script setup lang="ts">
import AppNavbar from "@/components/AppNavbar.vue"
</script>

<style>
.main {
  padding-top: 80px; /* must be larger than navbar height */
}
</style>
