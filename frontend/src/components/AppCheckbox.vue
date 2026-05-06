<script setup lang="ts">

const props = defineProps<{
  modelValue: boolean
  label?: string
}>()

const emit = defineEmits([
  "update:modelValue"
])

function toggle() {
  emit("update:modelValue", !props.modelValue)
}
</script>

<template>
  <label class="checkbox">
    <input
        type="checkbox"
        :checked="modelValue"
        @change="toggle"
    />

    <span class="box">
      <span class="check" v-if="modelValue">✓</span>
    </span>

    <span class="label" v-if="label">
      {{ label }}
    </span>
  </label>
</template>

<style scoped>

.checkbox {
  display: flex;
  align-items: center;
  gap: 10px;

  cursor: pointer;
  user-select: none;

  font-size: 15px;
}

/* Hide default checkbox */
input {
	opacity: 0;
	width: 20px;
	height: 20px;
	z-index: 2;
}

.box {
	position: absolute;
	width: 20px;
	height: 20px;

	border-radius: 6px;

	border: 2px solid var(--color-primary);

	display: flex;
	align-items: center;
	justify-content: center;

	transition: all 0.2s ease;
}

.check {
	position: absolute;
	padding-top: 1px;
	padding-left: 1px;
	font-size: 16px;
}

input:checked + .box {
	background: linear-gradient(
		135deg,
		var(--color-primary),
		var(--color-primary-light)
	);

	border-color: transparent;
}


/* Label */
.label {
  color: var(--color-text-light);
}

</style>
