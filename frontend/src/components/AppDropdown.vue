<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  modelValue?: string
  values: any[]
  placeholder?: string
  label?: string
  id?: string
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: "update:modelValue", value: string): void
}>()

const normalizedOptions = computed(() => {
  return props.values.map(item => {
    if (typeof item === 'object' && item !== null) {
      return {
        val: item.value || item.id || item.userId || '',
        lab: item.label || item.name || item.userName || ''
      }
    }
    return { val: String(item), lab: String(item) }
  })
})

function onInput(event: Event) {
  const target = event.target as HTMLSelectElement
  emit("update:modelValue", target.value)
}
</script>

<template>
  <div class="input-wrapper">
    <!-- The 'for' attribute looks for a matching 'id' on an input/select -->
    <label v-if="label" :for="id" class="label">{{ label }}</label>

    <select
        :id="id"
        class="input"
        :value="modelValue || ''"
        @change="onInput"
        :disabled="disabled"
    >
      <option value="">{{ placeholder || 'Select...' }}</option>
      <option v-for="opt in normalizedOptions" :key="opt.val" :value="opt.val">
        {{ opt.lab }}
      </option>
    </select>
  </div>
</template>

<style scoped>
.input-wrapper { display: flex; flex-direction: column; gap: 6px; width: 100%; }
.label { font-size: 14px; font-weight: 500; color: var(--color-secondary); }
.input {
  padding: 10px 14px; border-radius: 8px; border: 2px solid var(--color-primary-light);
  outline: none; font-size: 14px; transition: 0.2s ease; height: 3rem; width: 100%;
}
.input:focus { border-color: var(--color-primary); box-shadow: 0 0 0 3px rgba(243, 114, 44, 0.2); }
</style>