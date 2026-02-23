<template>
  <section class="section">
    <h2 v-if="$slots.title">
      <slot name="title"/>
    </h2>

    <slot />
  </section>
</template>

<style scoped>
.section {
  margin-top: 30px;
}
</style>
