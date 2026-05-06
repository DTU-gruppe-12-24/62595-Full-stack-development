<script setup lang="ts">
import {computed, ref, watch} from 'vue'

import AppContainer from '@/components/AppContainer.vue'
import AppSection from '@/components/AppSection.vue'
import AppCard from '@/components/AppCard.vue'
import AppInput from '@/components/AppInput.vue'
import AppDropdown from '@/components/AppDropdown.vue'
import AppButton from '@/components/AppButton.vue'
import AppCheckbox from '@/components/AppCheckbox.vue'
import AppText from '@/components/AppText.vue'
import AppDialog from '@/components/AppDialog.vue'
import AppConfirmDialog from '@/components/AppConfirmDialog.vue'
import type {Group} from '@/components/GroupSelector.vue'
import GroupSelector from '@/components/GroupSelector.vue'
import type {IngredientResult} from '@/components/IngredientSearch.vue'
import IngredientSearch from '@/components/IngredientSearch.vue'

import {apiFetch} from '@/utilities/apiFetch'
import {showError, showInfo, showSuccess} from '@/utilities/notifications'
import {Unit} from '@/model/RecipeIngredient'

interface ShoppingItem {
  id: string
  ingredientId: string
  ingredientName: string
  amount: number
  unit: Unit | null
  isBought: boolean
}

// Active group
const activeGroup = ref<Group | undefined>(undefined)

watch(activeGroup, async (newGroup) => {
  if (newGroup) {
    await loadItems()
    await loadGroupMembers()

    currentShopperId.value = (newGroup as any).currentShopper?.id || undefined;
  } else {
    items.value = []
    groupMembers.value = []
    currentShopperId.value = undefined
  }
})

// Items
const items = ref<ShoppingItem[]>([])
const loading = ref(false)

async function loadItems() {
  if (!activeGroup.value) { items.value = []; return }
  loading.value = true
  try {
    items.value = await apiFetch<ShoppingItem[]>(`/api/shopping-list/${activeGroup.value.id}`)
  } catch (e) {
    showError(e instanceof Error ? e.message : "" + e);
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
  } catch (e) { showError(e instanceof Error ? e.message : "" + e) }
}

async function deleteItem(id: string) {
  try {
    await apiFetch(`/api/shopping-list/item/${id}`, 'DELETE')
    items.value = items.value.filter(i => i.id !== id)
    showSuccess('Item removed from shopping list.')
  } catch (e) { showError(e instanceof Error ? e.message : "" + e) }
}

async function removeBought() {
  if (!activeGroup.value) return
  try {
    const removedCount = items.value.filter(i => i.isBought).length
    await apiFetch(`/api/shopping-list/${activeGroup.value.id}/bought`, 'DELETE')
    items.value = items.value.filter(i => !i.isBought)
    if (removedCount > 0) showSuccess(`Removed ${removedCount} bought item${removedCount === 1 ? '' : 's'}.`)
    else showInfo('No bought items to remove.')
  } catch (e) { showError(e instanceof Error ? e.message : "" + e) }
}

const showConfirmRemoveBought = ref(false)
const showConfirmClearList = ref(false)

function requestRemoveBought() {
  if (!activeGroup.value) return
  showConfirmRemoveBought.value = true
}

function requestClearList() {
  if (!activeGroup.value) return
  showConfirmClearList.value = true
}

async function confirmRemoveBought() {
  showConfirmRemoveBought.value = false
  await removeBought()
}

async function confirmClearList() {
  showConfirmClearList.value = false
  await clearList()
}

async function clearList() {
  if (!activeGroup.value) return
  try {
    await apiFetch(`/api/shopping-list/${activeGroup.value.id}/clear`, 'DELETE')
    items.value = []
    showSuccess('Shopping list cleared.')
  } catch (e) { showError(e instanceof Error ? e.message : "" + e) }
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
  showInfo('Shopping list exported.')
}

// Add item dialog
const showAddDialog = ref(false)
const selectedIngredient = ref<IngredientResult | null>(null)
const newAmount = ref<number | ''>('')
const newUnit = ref<`${Unit}`>('')

function openAddDialog() {
  selectedIngredient.value = null
  newAmount.value = ''
  newUnit.value = ''
  showAddDialog.value = true
}

async function submitAddItem() {
  if (!selectedIngredient.value) { showError('Please select or type an ingredient.'); return }
  if (!newAmount.value || Number(newAmount.value) <= 0) { showError('Please enter a valid amount.'); return }
  if (!activeGroup.value) return

  try {
    const item = await apiFetch<ShoppingItem, { ingredientName: string; amount: number; unit: `${Unit}` }>(
      `/api/shopping-list/${activeGroup.value.id}`,
      'POST',
      { ingredientName: selectedIngredient.value.ingredientName, amount: Number(newAmount.value), unit: newUnit.value }
    )
    const existing = items.value.findIndex(i => i.id === item.id)
    if (existing !== -1) items.value[existing] = item
    else items.value.push(item)
    showAddDialog.value = false
    showSuccess('Item added to shopping list.')
  } catch (e) {
    showError(e instanceof Error ? e.message : 'Failed to add item.')
  }
}

// Add custom ingredient dialog
const showCustomIngredientDialog = ref(false)
const customIngredient = ref({
  name: '',
  calories: '' as number | '',
  protein: '' as number | '',
  carbohydrates: '' as number | '',
  fat: '' as number | '',
  saturatedFat: '' as number | '',
  sugars: '' as number | '',
  salt: '' as number | '',
  price: '' as number | '',
})

function openCustomIngredientDialog() {
  customIngredient.value = {
    name: '', calories: '', protein: '', carbohydrates: '',
    fat: '', saturatedFat: '', sugars: '', salt: '', price: ''
  }
  showCustomIngredientDialog.value = true
}

function toOptionalFloat(val: number | ''): number | null {
  return val === '' ? null : Number(val)
}

async function submitCustomIngredient() {
  const name = customIngredient.value.name.trim()
  if (!name) { showError('Please enter an ingredient name.'); return }

  try {
    await apiFetch('/api/ingredients', 'POST', {
      name,
      calories:      toOptionalFloat(customIngredient.value.calories),
      protein:       toOptionalFloat(customIngredient.value.protein),
      carbohydrates: toOptionalFloat(customIngredient.value.carbohydrates),
      fat:           toOptionalFloat(customIngredient.value.fat),
      saturatedFat:  toOptionalFloat(customIngredient.value.saturatedFat),
      sugars:        toOptionalFloat(customIngredient.value.sugars),
      salt:          toOptionalFloat(customIngredient.value.salt),
      price:         toOptionalFloat(customIngredient.value.price),
    })
    showSuccess(`"${name}" has been added to the database.`)
    customIngredient.value.name = ''
  } catch (e) {
    showError(e instanceof Error ? e.message : 'Failed to add ingredient.')
  }
}

const groupMembers = ref<any[]>([])
const currentShopperId = ref<string | undefined>(undefined)

async function loadGroupMembers() {
  if (!activeGroup.value) return
  try {
    groupMembers.value = await apiFetch<any[]>(`/api/group/${activeGroup.value.id}/members`)
  } catch (e) {
    console.error("Failed to load members", e)
  }
}

async function updateGroupShopper() {
  if (!activeGroup.value) return
  try {
    const url = `/api/group/${activeGroup.value.id}/shopper?userId=${currentShopperId.value || ''}`
    await apiFetch(url, 'PATCH')
    showSuccess("Shopper updated")
  } catch (e) {
    showError("Failed to update shopper")
  }
}
const memberOptions = computed(() => {
  return groupMembers.value.map(m => ({
    id: m.user.id,
    name: m.user.name
  }))
})
</script>

<template>
  <AppContainer>
    <AppText variant="title" class="page-title">Shopping List</AppText>

    <!-- Group selector -->
    <AppSection>
      <AppCard>
        <GroupSelector v-model="activeGroup" persist />
      </AppCard>
    </AppSection>

    <!-- Everything below is hidden until a group is selected -->
    <template v-if="activeGroup">
      <AppSection v-if="activeGroup">
        <AppCard>
          <div class="shopper-info">
            <AppDropdown
                label="Designated Shopper"
                :values="memberOptions"
                v-model="currentShopperId"
                @update:model-value="updateGroupShopper"
            />
          </div>
        </AppCard>
      </AppSection>
      <!-- Actions -->
      <AppSection>
        <div class="actions">
          <AppButton variant="primary" @click="openAddDialog">Add item</AppButton>
          <AppButton variant="secondary" disabled>Generate shopping list (coming soon)</AppButton>
          <AppButton variant="secondary" @click="exportList">Export list</AppButton>
          <AppButton variant="danger" @click="requestRemoveBought">Remove bought</AppButton>
          <AppButton variant="danger" @click="requestClearList">Clear list</AppButton>
        </div>
      </AppSection>

      <!-- List -->
      <AppSection>
        <template #title>Items ({{ remainingCount }} left)</template>
        <AppCard class="list-card">
          <div v-if="loading" class="empty">Loading…</div>
          <div v-else-if="items.length === 0" class="empty">No items yet, add some above.</div>

          <div v-for="item in items" :key="item.id" class="list-item">
            <AppCheckbox
              :model-value="item.isBought"
              :label="formatItem(item)"
              :class="{ 'item-checked': item.isBought }"
              @update:model-value="toggleBought(item)"
            />
            <AppButton variant="ghost" class="delete-btn" @click="deleteItem(item.id)">✕</AppButton>
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
          <AppInput v-model="newAmount" label="Amount" id="AmountInput" type="number" placeholder="e.g. 500" />
          <AppDropdown
          		label="Unit (optional)"
				:values="Object.values(Unit)"
				id="UnitInput"
				v-model="newUnit"
				placeholder=""
			/>
        </div>
      </div>
      <template #footer>
        <AppButton variant="cancel" @click="showAddDialog = false">Cancel</AppButton>
        <AppButton variant="primary" @click="submitAddItem">Add</AppButton>
      </template>
    </AppDialog>

    <!-- Add custom ingredient button, shown outside the group guard -->
    <AppSection>
      <template #title>Database</template>
      <AppCard>
        <AppText variant="caption" class="database-help">
          Can't find an ingredient in the search? Add it to the database here.
        </AppText>
        <AppButton variant="secondary" @click="openCustomIngredientDialog">
          Add custom ingredient
        </AppButton>
      </AppCard>
    </AppSection>

  </AppContainer>

  <!-- Add custom ingredient dialog -->
    <AppDialog v-model="showCustomIngredientDialog" title="Add custom ingredient" width="560px">
      <div class="dialog-form">
        <AppInput v-model="customIngredient.name" label="Name" placeholder="e.g. Dragon fruit" />

      <AppText variant="caption" class="section-label">Nutritional values per 100g (optional)</AppText>

      <div class="amount-row">
        <AppInput v-model="customIngredient.calories"      label="Calories (kcal)" type="number" placeholder="e.g. 52" />
        <AppInput v-model="customIngredient.protein"       label="Protein (g)"     type="number" placeholder="e.g. 0.3" />
      </div>
      <div class="amount-row">
        <AppInput v-model="customIngredient.carbohydrates" label="Carbs (g)"       type="number" placeholder="e.g. 14" />
        <AppInput v-model="customIngredient.sugars"        label="Sugars (g)"      type="number" placeholder="e.g. 10" />
      </div>
      <div class="amount-row">
        <AppInput v-model="customIngredient.fat"           label="Fat (g)"         type="number" placeholder="e.g. 0.2" />
        <AppInput v-model="customIngredient.saturatedFat"  label="Saturated fat (g)" type="number" placeholder="e.g. 0.1" />
      </div>
      <div class="amount-row">
        <AppInput v-model="customIngredient.salt"          label="Salt (g)"        type="number" placeholder="e.g. 0.01" />
        <AppInput v-model="customIngredient.price"         label="Price (DKK)"     type="number" placeholder="e.g. 12.50" />
      </div>

    </div>
    <template #footer>
      <AppButton variant="cancel" @click="showCustomIngredientDialog = false">Cancel</AppButton>
      <AppButton variant="primary" @click="submitCustomIngredient">Add</AppButton>
    </template>
  </AppDialog>

  <AppConfirmDialog
    v-model="showConfirmRemoveBought"
    title="Remove bought items"
    message="This will remove all bought items from the list. This action cannot be undone."
    confirm-label="Remove bought items"
    @confirm="confirmRemoveBought"
  />

  <AppConfirmDialog
    v-model="showConfirmClearList"
    title="Clear shopping list"
    message="This will remove all items from the shopping list. This action cannot be undone."
    confirm-label="Clear list"
    @confirm="confirmClearList"
  />
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
.section-label { font-size: 13px; font-weight: 500; color: var(--color-secondary); margin: 4px 0 0; }
.database-help { margin-bottom: 12px; }

@media (max-width: 480px) {
  .amount-row { flex-direction: column; gap: 8px; }
}

.shopper-info { display: flex; align-items: center; gap: 12px; }
</style>
