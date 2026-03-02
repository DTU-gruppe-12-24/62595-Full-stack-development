<template>
  <button
      class="app-button"
      :class="[`app-button--${variant}`]"
  >
    <slot />
  </button>
</template>

<script setup lang="ts">
defineProps<{
  variant?: 'primary' | 'secondary' | 'ghost' | 'cancel'
}>()
</script>

<style scoped>
.app-button {
  padding: 10px 18px;
  border-radius: 8px;
  border-radius: var(--radius-md);
  border: none;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.app-button:active {
  transform: scale(0.98);
}

/* Variants */

.app-button--primary {
  background: var(--color-primary);
  color: white;
}
.app-button--primary:hover {
  background: var(--color-primary-light);
}

.app-button--secondary {
  background: var(--color-secondary);
  color: white;
}

.app-button--cancel {
  background: #e2e8f0;
  color: #475569;
}

.app-button--cancel:hover {
  background: #cbd5e1;
  color: #1e293b;
}

.app-button--ghost {
  background: transparent;
  color: var(--color-primary);
}
.app-button--ghost:hover {
  background: #f8fafc;
}
</style>