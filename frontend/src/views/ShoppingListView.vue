<script setup lang="ts">
import { ref, computed, watch } from 'vue'

import AppContainer from '@/components/AppContainer.vue'
import AppSection from '@/components/AppSection.vue'
import AppCard from '@/components/AppCard.vue'
import AppInput from '@/components/AppInput.vue'
import AppButton from '@/components/AppButton.vue'
import AppCheckbox from '@/components/AppCheckbox.vue'
import AppText from '@/components/AppText.vue'
import AppDialog from '@/components/AppDialog.vue'
import GroupSelector from '@/components/GroupSelector.vue'
import IngredientSearch from '@/components/IngredientSearch.vue'
import type { Group } from '@/components/GroupSelector.vue'
import type { IngredientResult } from '@/components/IngredientSearch.vue'

import { apiFetch } from '@/utilities/apiFetch'

interface ShoppingItem {
  id: string
  ingredientId: string
  ingredientName: string
  amount: number
  unit: string | null
  isBought: boolean
}

// Active group
const activeGroup = ref<Group | null>(null)

watch(activeGroup, () => loadItems())

// Items
const items = ref<ShoppingItem[]>([])
const loading = ref(false)

async function loadItems() {
  if (!activeGroup.value) { items.value = []; return }
  loading.value = true
  try {
    items.value = await apiFetch<ShoppingItem[]>(`/api/shopping-list/${activeGroup.value.id}`)
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const remainingCount = computed(() => items.value.filter(i => !i.isBought).length)

function formatItem(item: ShoppingItem): string {
  const amt = item.amount % 1 === 0 ? item.amount.toFixed(0) : String(item.amount)
  return item.unit ? `${item.ingredientName}, ${amt} ${item.unit}` : `${item.ingredientName}, ${amt}`
}

// Toggle / delete / remove bought / clear
async function toggleBought(item: ShoppingItem) {
  try {
    const updated = await apiFetch<ShoppingItem>(`/api/shopping-list/item/${item.id}/toggle`, 'PATCH')
    const index = items.value.findIndex(i => i.id === item.id)
    if (index !== -1) items.value[index] = updated
  } catch (e) { console.error(e) }
}

async function deleteItem(id: string) {
  try {
    await apiFetch(`/api/shopping-list/item/${id}`, 'DELETE')
    items.value = items.value.filter(i => i.id !== id)
  } catch (e) { console.error(e) }
}

async function removeBought() {
  if (!activeGroup.value) return
  try {
    await apiFetch(`/api/shopping-list/${activeGroup.value.id}/bought`, 'DELETE')
    items.value = items.value.filter(i => !i.isBought)
  } catch (e) { console.error(e) }
}

async function clearList() {
  if (!activeGroup.value) return
  try {
    await apiFetch(`/api/shopping-list/${activeGroup.value.id}/clear`, 'DELETE')
    items.value = []
  } catch (e) { console.error(e) }
}

// Export
function exportList() {
  const lines = ['## Shopping list', '']
  for (const item of items.value) {
    lines.push(`- ${item.isBought ? '[x]' : '[ ]'} ${formatItem(item)}`)
  }
  const blob = new Blob([lines.join('\n')], { type: 'text/plain' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'shopping-list.txt'
  a.click()
  URL.revokeObjectURL(url)
}

// Add item dialog
const showAddDialog = ref(false)
const selectedIngredient = ref<IngredientResult | null>(null)
const newAmount = ref<number | ''>('')
const newUnit = ref('')
const addError = ref('')

function openAddDialog() {
  selectedIngredient.value = null
  newAmount.value = ''
  newUnit.value = ''
  addError.value = ''
  showAddDialog.value = true
}

async function submitAddItem() {
  addError.value = ''
  if (!selectedIngredient.value) { addError.value = 'Please select or type an ingredient.'; return }
  if (!newAmount.value || Number(newAmount.value) <= 0) { addError.value = 'Please enter a valid amount.'; return }
  if (!activeGroup.value) return

  try {
    const item = await apiFetch<ShoppingItem, { ingredientName: string; amount: number; unit: string }>(
      `/api/shopping-list/${activeGroup.value.id}`,
      'POST',
      { ingredientName: selectedIngredient.value.ingredientName, amount: Number(newAmount.value), unit: newUnit.value.trim() }
    )
    const existing = items.value.findIndex(i => i.id === item.id)
    if (existing !== -1) items.value[existing] = item
    else items.value.push(item)
    showAddDialog.value = false
  } catch (e) {
    addError.value = e instanceof Error ? e.message : 'Failed to add item.'
  }
}

const showAddRecipeDialog = ref(false)
const showGenerateDialog = ref(false)
</script>

<template>
  <AppContainer>
    <AppText variant="title" class="page-title">Shopping List</AppText>

    <!-- Group selector -->
    <AppSection>
      <template #title>Group</template>
      <AppCard>
        <GroupSelector v-model="activeGroup" />
      </AppCard>
    </AppSection>

    <!-- Everything below is hidden until a group is selected -->
    <template v-if="activeGroup">

      <!-- Actions -->
      <AppSection>
        <div class="actions">
          <AppButton variant="primary" @click="openAddDialog">Add item</AppButton>
          <AppButton variant="secondary" @click="showAddRecipeDialog = true">Add recipe</AppButton>
          <AppButton variant="secondary" @click="showGenerateDialog = true">Generate shopping list</AppButton>
          <AppButton variant="secondary" @click="exportList">Export list</AppButton>
          <AppButton variant="cancel" @click="removeBought">Remove bought</AppButton>
          <AppButton variant="cancel" @click="clearList">Clear list</AppButton>
        </div>
      </AppSection>

      <!-- List -->
      <AppSection>
        <template #title>Items ({{ remainingCount }} left)</template>
        <AppCard class="list-card">
          <div v-if="loading" class="empty">Loading…</div>
          <div v-else-if="items.length === 0" class="empty">No items yet — add some above.</div>

          <div v-for="item in items" :key="item.id" class="list-item">
            <AppCheckbox
              :model-value="item.isBought"
              :label="formatItem(item)"
              :class="{ 'item-checked': item.isBought }"
              @update:model-value="toggleBought(item)"
            />
            <button class="delete-btn" @click="deleteItem(item.id)">✕</button>
          </div>
        </AppCard>
      </AppSection>

    </template>

    <!-- Add item dialog -->
    <AppDialog v-model="showAddDialog" title="Add ingredient">
      <div class="dialog-form">
        <IngredientSearch
          v-model="selectedIngredient"
          label="Ingredient"
          placeholder="Search e.g. mayonnaise, milk, sugar…"
        />
        <div class="amount-row">
          <AppInput v-model="newAmount" label="Amount" type="number" placeholder="e.g. 500" />
          <AppInput v-model="newUnit" label="Unit (optional)" placeholder="e.g. grams, loafs" />
        </div>
        <p v-if="addError" class="error-text">{{ addError }}</p>
      </div>
      <template #footer>
        <AppButton variant="cancel" @click="showAddDialog = false">Cancel</AppButton>
        <AppButton variant="primary" @click="submitAddItem">Add</AppButton>
      </template>
    </AppDialog>

    <!-- Add recipe — not implemented -->
    <AppDialog v-model="showAddRecipeDialog" title="Add recipe">
      <p>Not implemented yet.</p>
      <template #footer>
        <AppButton variant="secondary" @click="showAddRecipeDialog = false">Close</AppButton>
      </template>
    </AppDialog>

    <!-- Generate — not implemented -->
    <AppDialog v-model="showGenerateDialog" title="Generate shopping list">
      <p>Not implemented yet.</p>
      <template #footer>
        <AppButton variant="secondary" @click="showGenerateDialog = false">Close</AppButton>
      </template>
    </AppDialog>

  </AppContainer>
</template>

<style scoped>
.page-title { margin-bottom: 24px; }

.actions { display: flex; flex-wrap: wrap; gap: 10px; }

.list-card { margin-top: 10px; }

.list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f9c74f20;
}

.list-item:last-child { border-bottom: none; }

.item-checked :deep(label) { text-decoration: line-through; opacity: 0.5; }

.delete-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #f3722c;
  font-size: 18px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background 0.2s;
}

.delete-btn:hover { background: #fff5f0; }

.empty { color: #888; text-align: center; padding: 30px; font-style: italic; }

.dialog-form { display: flex; flex-direction: column; gap: 16px; min-height: 280px; }

.amount-row { display: flex; gap: 12px; }
.amount-row > * { flex: 1; }

.error-text { color: #c0392b; font-size: 0.875rem; margin: 0; }
</style>
