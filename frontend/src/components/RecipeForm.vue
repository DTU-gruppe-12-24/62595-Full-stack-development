<script setup lang="ts">
import AppInput from "@/components/AppInput.vue"
import AppButton from "@/components/AppButton.vue"
import AppCard from "@/components/AppCard.vue"
import AppDropdown from "@/components/AppDropdown.vue"
import IngredientSearch from "@/components/IngredientSearch.vue"
import type { IngredientResult } from "@/components/IngredientSearch.vue"
import GroupSelector from "./GroupSelector.vue"
import type { Group } from "@/model/Group"
import { Unit } from "@/model/RecipeIngredient"

export interface IngredientLine {
  selected: IngredientResult | null
  amount: number | ""
  unit: string
}

export interface RecipeFormData {
  name: string
  description: string
  instructions: string
  mealType: string
  servings: number | null
  prepTimeMinutes: number | null
}

const props = defineProps<{
  modelValue: RecipeFormData
  ingredients: IngredientLine[]
  group: Group | null
  isSaving: boolean
  submitLabel: string
  canChangeGroup: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: RecipeFormData]
  'update:ingredients': [value: IngredientLine[]]
  'update:group': [value: Group | null]
  'submit': []
  'cancel': []
}>()

function updateField(field: keyof RecipeFormData, value: unknown) {
  emit('update:modelValue', { ...props.modelValue, [field]: value })
}

function addIngredientLine() {
  emit('update:ingredients', [...props.ingredients, { selected: null, amount: "", unit: "" }])
}

function removeIngredientLine(index: number) {
  const updated = props.ingredients.filter((_, i) => i !== index)
  emit('update:ingredients', updated)
}

function updateIngredientLine(index: number, updated: IngredientLine) {
  const lines = props.ingredients.map((line, i) => i === index ? updated : line)
  emit('update:ingredients', lines)
}

function updateGroup(group: Group | null) {
    emit('update:group', group)
}
</script>

<template>
  <AppCard>
    <div class="form">
      <AppInput
        :model-value="modelValue.name"
        label="Name"
        placeholder="Recipe name"
        @update:model-value="updateField('name', $event)"
      />
      <AppInput
        :model-value="modelValue.description"
        label="Description"
        placeholder="Description"
        type="textarea"
        @update:model-value="updateField('description', $event)"
      />
      <AppInput
        :model-value="modelValue.instructions"
        label="Instructions"
        placeholder="Step by step instructions"
        type="textarea"
        @update:model-value="updateField('instructions', $event)"
        class="h-48"
      />

      <GroupSelector
      	v-if="canChangeGroup"
        :model-value="group"
        label="Group"
        placeholder="Group..."
        @update:model-value="updateGroup"
        allowNull
      />

      <div class="row">
        <AppInput
          :model-value="modelValue.mealType"
          label="Meal type"
          placeholder="e.g. Dinner"
          @update:model-value="updateField('mealType', $event)"
        />
        <AppInput
          :model-value="modelValue.servings"
          label="Servings"
          type="number"
          placeholder="e.g. 4"
          @update:model-value="updateField('servings', $event)"
        />
        <AppInput
          :model-value="modelValue.prepTimeMinutes"
          label="Prep time (min)"
          type="number"
          placeholder="e.g. 30"
          @update:model-value="updateField('prepTimeMinutes', $event)"
        />
      </div>

      <p class="section-label">Ingredients</p>

      <div
        v-for="(line, index) in ingredients"
        :key="index"
        class="ingredient-line"
      >
        <div class="ingredient-name">
          <IngredientSearch
            :model-value="line.selected"
            placeholder="Search ingredient…"
            @update:model-value="updateIngredientLine(index, { ...line, selected: $event })"
          />
        </div>
        <div class="ingredient-amount">
          <AppInput
            :model-value="line.amount"
            type="number"
            placeholder="Amount"
            @update:model-value="updateIngredientLine(index, { ...line, amount: $event as number | '' })"
          />
        </div>
        <div class="ingredient-unit">
          <AppDropdown
				:values="Object.values(Unit)"
				v-model="line.unit"
				placeholder=""
				@update:model-value="updateIngredientLine(index, { ...line, unit: $event })"
			/>
        </div>
        <button class="remove-btn" @click="removeIngredientLine(index)">✕</button>
      </div>

      <AppButton variant="secondary" @click="addIngredientLine">+ Add ingredient</AppButton>
    </div>

    <template #footer>
      <AppButton variant="secondary" @click="emit('cancel')">Cancel</AppButton>
      <AppButton variant="primary" :disabled="isSaving" @click="emit('submit')">
        {{ isSaving ? 'Saving...' : submitLabel }}
      </AppButton>
    </template>
  </AppCard>
</template>

<style scoped>
.form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.row {
  display: flex;
  gap: 12px;
  min-width: 0;
}

.row > * { flex: 1; min-width: 0; }

.section-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-secondary);
  margin: 4px 0 0;
}

.ingredient-line {
  display: flex;
  gap: 8px;
  align-items: flex-end;
  width: 100%;
  min-width: 0;
}

.ingredient-name {
  flex: 5;
  min-width: 0;
}

.ingredient-amount {
  flex: 2;
  min-width: 0;
}

.ingredient-unit {
  flex: 2;
  min-width: 0;
}

.remove-btn {
  flex: 1;
  min-width: 0;

  display: flex;
  justify-content: center;
  align-items: center;

  background: none;
  border: none;
  cursor: pointer;
  color: #f3722c;
  font-size: 16px;
  padding: 8px 0;
  border-radius: 4px;
  margin-bottom: 2px;
  transition: background 0.2s;
}

.remove-btn:hover { background: #fff5f0; }

</style>
