<template>
  <div
      :class="['card', { hover }]"
      :style="{ '--card-width': width || '100%' }"
  >
    <div :class="['card-body', { 'is-flex': flex }]">
      <slot />
    </div>

    <div v-if="$slots.footer" class="card-footer">
      <slot name="footer" />
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  hover?: boolean,
  width?: string,
  flex?: boolean
}>()
</script>

<style scoped>
.card {
  background: white;
  border-radius: 16px;
  width: var(--card-width);
  box-shadow:
      0 4px 12px rgba(0, 0, 0, 0.05),
      0 2px 4px rgba(0, 0, 0, 0.03);
  border: 1px solid #f9c74f20;
  overflow: hidden; /* Ensures footer background doesn't bleed over radius */
  display: flex;
  flex-direction: column;
  transition: all 0.25s ease;
}

.card.hover:hover {
  transform: translateY(-4px);
  box-shadow:
      0 10px 25px rgba(243, 114, 44, 0.15),
      0 4px 10px rgba(0, 0, 0, 0.08);
  border-color: #f3722c40;
}

.card-body {
  padding: 20px;
  flex-grow: 1; /* Ensures body fills space if card has a fixed height */
}

/* Fixes the "weird gap" at the top/bottom */
.card-body > :first-child { margin-top: 0; }
.card-body > :last-child { margin-bottom: 0; }

.card-footer {
  padding: 12px 20px;
  background: #f8f9fa;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end; /* Aligns button to the right by default */
}
.card-body.is-flex {
  display: flex;
  gap: 12px;
  align-items: center;
}
</style>