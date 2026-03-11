<script setup lang="ts">

defineProps<{
  modelValue?: unknown
  placeholder?: string
  label?: string
  type?: string
  min?: string
  max?: string
}>()

const emit = defineEmits<{
  (e: "update:modelValue", value: unknown): void
}>()

function onInput(event: Event) {
  const target = event.target as HTMLInputElement
  emit("update:modelValue", target.value)
}
</script>

<template>
  <div class="input-wrapper">
    <label v-if="label" class="label">
      {{ label }}
    </label>

    <input
        class="input"
        :type="type || 'text'"
        :placeholder="placeholder"
        :value="modelValue"
        :min="min"
        :max="max"
        @input="onInput"
    />
  </div>
</template>

<style scoped>
.input-wrapper {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.label {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-secondary);
}

.input {
  padding: 10px 14px;
  border-radius: 8px;
  border: 2px solid var(--color-primary-light);
  outline: none;
  font-size: 14px;
  transition: 0.2s ease;
}

.input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(243, 114, 44, 0.2);
}
</style>
