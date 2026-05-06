<script setup lang="ts">

const props = defineProps<{
  modelValue?: string
  values: string[]
  placeholder?: string
  label?: string
  id?: string
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: "update:modelValue", value: string): void
}>()

function onInput(event: Event) {
  const target = event.target as HTMLInputElement
  emit("update:modelValue", target.value)
}
</script>

<template>
  <div class="input-wrapper">
    <label v-if="label" :for="id" class="label">
      {{ label }}
    </label>

    <select
    	class="input"
	    :placeholder="placeholder"
	    :value="modelValue"
					:id="id"
	    @input="onInput"
		:disabled="disabled"
    >
    	<option v-if="modelValue && !values.includes(modelValue)" :value="modelValue">{{modelValue}}</option>
    	<option v-for="value in values" :value="value">{{value}}</option>
    </select>
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
  height: 3rem;
}

.input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(243, 114, 44, 0.2);
}

.input:disabled {
	border-color: var(--color-text-light);
	background-color: var(--color-gray-300);
	cursor: not-allowed;
}
</style>
