<template>
  <div v-if="modelValue" class="overlay" @click="close">
    <div
        class="dialog"
        @click.stop
        :style="{ '--dialog-width': width || '500px' }"
    >
      <header class="header">
        <h3>{{ title }}</h3>
        <button class="close-btn" @click="close">✕</button>
      </header>

      <div class="content">
        <slot />
      </div>

      <footer v-if="$slots.footer" class="footer">
        <slot name="footer" />
      </footer>
    </div>
  </div>
</template>

<script setup>
defineProps({
  modelValue: Boolean,
  title: String,
  width: String
})

const emit = defineEmits(['update:modelValue'])
const close = () => emit('update:modelValue', false)
</script>

<style scoped>
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
}

.dialog {
  background: white;
  border-radius: 16px;
  width: var(--dialog-width);
  max-width: 95vw;
  display: flex;
  flex-direction: column;

  box-shadow:
      0 20px 25px -5px rgba(0, 0, 0, 0.1),
      0 10px 10px -5px rgba(0, 0, 0, 0.04);

  border: 1px solid #f9c74f20;
  overflow: hidden;
}

.header {
  padding: 20px;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h3 {
  margin: 0;
  font-size: 1.25rem;
}

.content {
  padding: 20px;
  overflow-y: auto;
  max-height: 70vh;
}

.content > :first-child { margin-top: 0; }
.content > :last-child { margin-bottom: 0; }

.footer {
  padding: 16px 20px;
  background: #f8fafc;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.close-btn {
  background: #f1f5f9;
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  transition: background 0.2s;
}

.close-btn:hover { background: #e2e8f0; }
</style>