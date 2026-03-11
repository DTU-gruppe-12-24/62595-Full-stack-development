<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { apiFetch } from '@/utilities/apiFetch'

export interface Group {
  id: string
  name: string
}

const STORAGE_KEY = 'activeGroupId'

const emit = defineEmits<{
  'update:modelValue': [value: Group | null]
}>()

defineProps<{
  modelValue: Group | null
}>()

const groups = ref<Group[]>([])
const selectedId = ref<string | null>(localStorage.getItem(STORAGE_KEY))

onMounted(async () => {
  try {
    groups.value = await apiFetch<Group[]>('/api/group/me')

    // Validate saved selection still exists
    if (selectedId.value && !groups.value.find(g => g.id === selectedId.value)) {
      selectedId.value = null
    }

    // Auto-select first group if nothing selected
    if (!selectedId.value && groups.value.length > 0) {
      selectedId.value = groups.value[0]?.id ?? null
    }

    persistAndEmit()
  } catch (e) {
    console.error(e)
  }
})

function onChange() {
  persistAndEmit()
}

function persistAndEmit() {
  if (selectedId.value) {
    localStorage.setItem(STORAGE_KEY, selectedId.value)
  } else {
    localStorage.removeItem(STORAGE_KEY)
  }
  const group = groups.value.find(g => g.id === selectedId.value) ?? null
  emit('update:modelValue', group)
}
</script>

<template>
  <div class="group-selector">
    <p v-if="groups.length === 0" class="empty">
      You don't belong to any groups yet.
    </p>

    <template v-else>
      <label class="label" for="group-select">Active group</label>
      <select
        id="group-select"
        v-model="selectedId"
        class="select"
        @change="onChange"
      >
        <option v-for="g in groups" :key="g.id" :value="g.id">
          {{ g.name }}
        </option>
      </select>
    </template>
  </div>
</template>

<style scoped>
.group-selector {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.label {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-secondary);
}

.select {
  padding: 10px 14px;
  border-radius: 8px;
  border: 2px solid var(--color-primary-light);
  font-size: 14px;
  background: white;
  cursor: pointer;
  max-width: 300px;
  outline: none;
  transition: 0.2s ease;
}

.select:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(243, 114, 44, 0.2);
}

.empty {
  font-size: 14px;
  color: #888;
  margin: 0;
  font-style: italic;
}
</style>
