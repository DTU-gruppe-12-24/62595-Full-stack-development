<script setup lang="ts">
import { ref, watch } from 'vue'
import { apiFetch } from '@/utilities/apiFetch'

export interface IngredientResult {
  ingredientId: string
  ingredientName: string
}

const props = defineProps<{
  modelValue: IngredientResult | null
  placeholder?: string
  label?: string
}>()

const emit = defineEmits<{
  'update:modelValue': [value: IngredientResult | null]
}>()

const query = ref(props.modelValue?.ingredientName ?? '')
const suggestions = ref<IngredientResult[]>([])
const showSuggestions = ref(false)
let searchTimeout: ReturnType<typeof setTimeout> | null = null

watch(() => props.modelValue, (val) => {
  if (val === null) query.value = ''
  else query.value = val.ingredientName ?? ''
})

function onInput() {
  emit('update:modelValue', null)
  showSuggestions.value = true
  updateDropdownPosition()

  if (searchTimeout) clearTimeout(searchTimeout)

  if (!query.value.trim()) {
    suggestions.value = []
    return
  }

  searchTimeout = setTimeout(async () => {
    try {
      suggestions.value = await apiFetch<IngredientResult[]>(
        `/api/shopping-list/ingredients/search?q=${encodeURIComponent(query.value)}`
      )
    } catch (e) {
      console.error(e)
      suggestions.value = []
    }
  }, 250)
}

function select(suggestion: IngredientResult) {
  query.value = suggestion.ingredientName
  suggestions.value = []
  showSuggestions.value = false
  emit('update:modelValue', suggestion)
}

const inputRef = ref<HTMLInputElement | null>(null)
const dropdownStyle = ref({})

function updateDropdownPosition() {
  if (!inputRef.value) return
  const rect = inputRef.value.getBoundingClientRect()
  dropdownStyle.value = {
    position: 'fixed',
    top: rect.bottom + 4 + 'px',
    left: rect.left + 'px',
    width: rect.width + 'px',
    zIndex: '9999',
  }
}

function onBlur() {
  setTimeout(() => { showSuggestions.value = false }, 150)
}
</script>

<template>
  <div class="ingredient-search">
    <label v-if="label" class="label" for="ingredient-search-input">{{ label }}</label>

    <div class="input-wrap">
      <input
        id="ingredient-search-input"
        ref="inputRef"
        class="input"
        type="text"
        :placeholder="placeholder ?? 'Search ingredients…'"
        :value="query"
        autocomplete="off"
        aria-autocomplete="list"
        :aria-expanded="showSuggestions && suggestions.length > 0"
        aria-controls="ingredient-suggestions"
        @input="query = ($event.target as HTMLInputElement).value; onInput()"
        @blur="onBlur"
        @focus="showSuggestions = true; updateDropdownPosition()"
      />

      <Teleport to="body">
        <ul
          v-if="showSuggestions && suggestions.length > 0"
          id="ingredient-suggestions"
          class="suggestions"
          :style="dropdownStyle"
        >
          <li
            v-for="s in suggestions"
            :key="s.ingredientId"
            class="suggestion-item"
            @mousedown.prevent="select(s)"
          >
            {{ s.ingredientName }}
          </li>
        </ul>

        <p
          v-if="showSuggestions && query.trim() && suggestions.length === 0"
          class="no-results"
          :style="dropdownStyle"
        >
          "{{ query }}" is not a recognised ingredient — it will be created when added
        </p>
      </Teleport>
    </div>
  </div>
</template>

<style scoped>
.ingredient-search {
  display: flex;
  flex-direction: column;
  gap: 6px;
  position: relative;
}

.label {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-secondary);
}

.input-wrap {
  position: relative;
}

.input {
  width: 100%;
  padding: 10px 14px;
  border-radius: 8px;
  border: 2px solid var(--color-primary-light);
  outline: none;
  font-size: 14px;
  transition: 0.2s ease;
  box-sizing: border-box;
}

.input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(243, 114, 44, 0.2);
}

.suggestions {
  background: white;
  border: 2px solid var(--color-primary-light);
  border-radius: 8px;
  list-style: none;
  margin: 0;
  padding: 4px 0;
  max-height: 220px;
  overflow-y: auto;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.suggestion-item {
  padding: 9px 14px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.15s;
}

.suggestion-item:hover {
  background: var(--color-primary-light, #fff3ec);
}

.no-results {
  background: white;
  border: 2px solid var(--color-primary-light);
  border-radius: 8px;
  padding: 9px 14px;
  font-size: 13px;
  color: #888;
  margin: 0;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}
</style>
