<template>
  <div class="container">
    <slot />
  </div>
</template>

<style scoped>
.container {
  width: 100%;
  max-width: 1100px;
  margin: auto;
  padding: 24px;
}

@media (max-width: 640px) {
  .container {
    padding: 16px;
  }
}
</style>
