
<template>
  <component
      :is="tag"
      :class="['app-text', `app-text--${variant}`]"
  >
    <slot />
  </component>
</template>

<script setup lang="ts">
defineProps({
  variant: {
    type: String,
    default: 'body'
  },
  tag: {
    type: String,
    default: 'p'
  }
})
</script>

<style scoped>
.app-text--title {
  font-size: 28px;
  font-weight: 700;
}

.app-text--subtitle {
  font-size: 20px;
  font-weight: 600;
}

.app-text--body {
  font-size: 16px;
}

.app-text--caption {
  font-size: 14px;
  color: var(--color-text-light);
}
</style>
