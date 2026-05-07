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

interface MealPlanEntry {
  id: string
  recipeId: string
  recipeName: string
  scheduledDate: string
  mealSlot: string
}

// Active group
const activeGroup = ref<Group | undefined>(undefined)

watch(activeGroup, async (newGroup) => {
  if (newGroup) {
    await loadItems()
    await loadGroupMembers()
    currentShopperId.value = (newGroup as any).currentShopper?.id || undefined
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
    showError(e instanceof Error ? e.message : '' + e)
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
  } catch (e) { showError(e instanceof Error ? e.message : '' + e) }
}

async function deleteItem(id: string) {
  try {
    await apiFetch(`/api/shopping-list/item/${id}`, 'DELETE')
    items.value = items.value.filter(i => i.id !== id)
    showSuccess('Item removed from shopping list.')
  } catch (e) { showError(e instanceof Error ? e.message : '' + e) }
}

async function removeBought() {
  if (!activeGroup.value) return
  try {
    const removedCount = items.value.filter(i => i.isBought).length
    await apiFetch(`/api/shopping-list/${activeGroup.value.id}/bought`, 'DELETE')
    items.value = items.value.filter(i => !i.isBought)
    if (removedCount > 0) showSuccess(`Removed ${removedCount} bought item${removedCount === 1 ? '' : 's'}.`)
    else showInfo('No bought items to remove.')
  } catch (e) { showError(e instanceof Error ? e.message : '' + e) }
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
  } catch (e) { showError(e instanceof Error ? e.message : '' + e) }
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

const TOTAL_DAYS = 14

const showGenerateDialog = ref(false)
const rangeStart = ref<number | null>(null)  // offset from today, 0-based
const rangeEnd = ref<number | null>(null)    // offset from today, 0-based
const previewMeals = ref<MealPlanEntry[]>([])
const previewLoading = ref(false)
const generating = ref(false)

interface DayDescriptor {
  offset: number
  label: string
  dateLabel: string
}

const days = computed<DayDescriptor[]>(() => {
  const result: DayDescriptor[] = []
  const base = new Date()
  for (let i = 0; i < TOTAL_DAYS; i++) {
    const d = new Date(base)
    d.setDate(base.getDate() + i)
    result.push({
      offset: i,
      label: i === 0 ? 'Today' : d.toLocaleDateString('en-GB', { weekday: 'short' }),
      dateLabel: d.toLocaleDateString('en-GB', { day: 'numeric', month: 'numeric' }),
    })
  }
  return result
})

function isDayInRange(offset: number): boolean {
  if (rangeStart.value === null) return false
  if (rangeEnd.value === null) return offset === rangeStart.value
  return offset >= rangeStart.value && offset <= rangeEnd.value
}

function isDayRangeStart(offset: number): boolean {
  return offset === rangeStart.value
}

function isDayRangeEnd(offset: number): boolean {
  return rangeEnd.value !== null && offset === rangeEnd.value
}

function selectDay(offset: number) {
  if (rangeStart.value === null || rangeEnd.value !== null) {
    rangeStart.value = offset
    rangeEnd.value = null
    return
  }
  if (offset < rangeStart.value) {
    rangeStart.value = offset
    rangeEnd.value = null
    return
  }
  if (offset === rangeStart.value) {
    rangeStart.value = null
    rangeEnd.value = null
    return
  }
  rangeEnd.value = offset
}

const rangeLabel = computed(() => {
  if (rangeStart.value === null) return 'Select a start day'

  const startDay = days.value[rangeStart.value]
  if (!startDay) return ''

  if (rangeEnd.value === null) {
    return `${startDay.label} ${startDay.dateLabel} (Single day)`
  }

  const endDay = days.value[rangeEnd.value]
  if (!endDay) return `${startDay.label} ${startDay.dateLabel}`

  const count = rangeEnd.value - rangeStart.value + 1

  return `${startDay.label} ${startDay.dateLabel} → ${endDay.label} ${endDay.dateLabel} (${count} day${count === 1 ? '' : 's'})`
})

const canConfirm = computed(() =>
    rangeStart.value !== null && previewMeals.value.length > 0 && !generating.value
)

watch([rangeStart, rangeEnd], () => {
  if (rangeStart.value !== null) {
    fetchMealPreview()
  } else {
    previewMeals.value = []
  }
})

function openGenerateDialog() {
  rangeStart.value = null
  rangeEnd.value = null
  previewMeals.value = []
  showGenerateDialog.value = true
}

function formatMealDate(dateStr: string): string {
  const d = new Date(dateStr + 'T00:00:00')
  return d.toLocaleDateString('en-GB', { weekday: 'short', day: 'numeric', month: 'short' })
}

function capitalise(str: string): string {
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase()
}

let previewAbort: AbortController | null = null

async function fetchMealPreview() {
  if (!activeGroup.value || rangeStart.value === null) return

  if (previewAbort) previewAbort.abort()
  previewAbort = new AbortController()

  previewLoading.value = true
  try {
    const base = new Date()
    const startDate = new Date(base)
    startDate.setDate(base.getDate() + rangeStart.value)

    const endDate = new Date(base)
    const endOffset = rangeEnd.value ?? rangeStart.value
    endDate.setDate(base.getDate() + endOffset)

    const fmt = (d: Date) => d.toISOString().split('T')[0]
    const meals = await apiFetch<MealPlanEntry[]>(
        `/api/meal-plans/range?groupId=${activeGroup.value.id}&start=${fmt(startDate)}&end=${fmt(endDate)}`
    )
    previewMeals.value = meals.sort((a, b) =>
        a.scheduledDate.localeCompare(b.scheduledDate) || a.mealSlot.localeCompare(b.mealSlot)
    )
  } catch (e: any) {
    if (e?.name !== 'AbortError') {
      showError(e instanceof Error ? e.message : 'Failed to load meal plan preview.')
    }
  } finally {
    previewLoading.value = false
  }
}
async function submitGenerateFromMealPlan() {
  if (!activeGroup.value || rangeStart.value === null) return
  generating.value = true

  const endDay = rangeEnd.value ?? rangeStart.value

  try {
    items.value = await apiFetch<ShoppingItem[]>(
        `/api/shopping-list/${activeGroup.value.id}/from-meal-plan?startDay=${rangeStart.value}&endDay=${endDay}`,
        'POST'
    )
    showGenerateDialog.value = false
    const count = previewMeals.value.length
    showSuccess(`Added ingredients from ${count} meal${count === 1 ? '' : 's'} to the shopping list.`)
  } catch (e) {
    showError(e instanceof Error ? e.message : 'Failed to generate shopping list.')
  } finally {
    generating.value = false
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
    console.error('Failed to load members', e)
  }
}

async function updateGroupShopper() {
  if (!activeGroup.value) return
  try {
    const url = `/api/group/${activeGroup.value.id}/shopper?userId=${currentShopperId.value || ''}`
    await apiFetch(url, 'PATCH')
    showSuccess('Shopper updated')
  } catch (e) {
    showError('Failed to update shopper')
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

    <AppSection>
      <AppCard>
        <GroupSelector v-model="activeGroup" persist />
      </AppCard>
    </AppSection>

    <template v-if="activeGroup">
      <AppSection>
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

      <AppSection>
        <div class="actions">
          <AppButton variant="primary" @click="openAddDialog">Add item</AppButton>
          <AppButton variant="secondary" @click="openGenerateDialog">Generate shopping list</AppButton>
          <AppButton variant="secondary" @click="exportList">Export list</AppButton>
          <AppButton variant="danger" @click="requestRemoveBought">Remove bought</AppButton>
          <AppButton variant="danger" @click="requestClearList">Clear list</AppButton>
        </div>
      </AppSection>

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

    <!-- Generate from meal plan dialog -->
    <AppDialog v-model="showGenerateDialog" title="Generate shopping list">
      <div class="generate-form">

        <p class="generate-help">Select a start and end day to add ingredients from planned meals to your shopping list.</p>

        <div class="day-grid" role="group" aria-label="Select day range">
          <div
              v-for="day in days"
              :key="day.offset"
              class="day-cell"
              :style="{
              background: isDayRangeStart(day.offset) || isDayRangeEnd(day.offset)
                ? 'var(--color-primary-soft)'
                : isDayInRange(day.offset)
                  ? '#fff7e6'
                  : 'var(--color-background)',
              borderColor: isDayInRange(day.offset)
                ? 'var(--color-primary-soft)'
                : 'var(--color-border)',
              fontWeight: isDayRangeStart(day.offset) || isDayRangeEnd(day.offset) ? '700' : '400',
            }"
              :aria-pressed="isDayInRange(day.offset)"
              :aria-label="`${day.label} ${day.dateLabel}`"
              @click="selectDay(day.offset)"
          >
            <span class="day-name">{{ day.label }}</span>
            <span class="day-date">{{ day.dateLabel }}</span>
          </div>
        </div>

        <p class="range-label" :class="{ 'range-label--active': rangeStart !== null }">
          {{ rangeLabel }}
        </p>

        <div class="preview-section">
          <p class="preview-heading">Meals in range</p>

          <div class="meal-list-wrapper">
            <Transition name="fade-slide" mode="out-in">
              <p v-if="rangeStart === null" key="empty-prompt" class="preview-empty">
                Select a range above to preview meals.
              </p>
              <p v-else-if="previewLoading" key="loading" class="preview-empty">
                Loading…
              </p>
              <p v-else-if="previewMeals.length === 0" key="no-meals" class="preview-empty">
                No meals planned in this range.
              </p>
              <div v-else key="meal-list" class="meal-list">
                <div
                    v-for="meal in previewMeals"
                    :key="meal.id"
                    class="meal-item"
                >
                  <span class="meal-date">{{ formatMealDate(meal.scheduledDate) }}</span>
                  <span class="meal-slot">{{ capitalise(meal.mealSlot) }}</span>
                  <span class="meal-recipe">{{ meal.recipeName }}</span>
                </div>
              </div>
            </Transition>
          </div>
        </div>

      </div>

      <template #footer>
        <AppButton variant="cancel" @click="showGenerateDialog = false">Cancel</AppButton>
        <AppButton
            variant="primary"
            :disabled="!canConfirm"
            @click="submitGenerateFromMealPlan"
        >
          {{ generating ? 'Adding…' : 'Add ingredients' }}
        </AppButton>
      </template>
    </AppDialog>

    <!-- Database section -->
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

.dialog-form { display: flex; flex-direction: column; gap: 16px; }

.amount-row { display: flex; gap: 12px; }
.amount-row > * { flex: 1; }
.section-label { font-size: 13px; font-weight: 500; color: var(--color-secondary); }
.database-help { margin-bottom: 12px; }

@media (max-width: 480px) {
  .amount-row { flex-direction: column; gap: 8px; }
}

.shopper-info { display: flex; align-items: center; gap: 12px; }

.generate-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 460px;
  max-width: 100%;
}

.generate-help {
  font-size: 13px;
  color: var(--color-text-light);
  margin: 0;
}

.day-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
  margin-bottom: 12px;
}

.range-label {
  font-size: 13px;
  color: var(--color-text-light);
  text-align: center;
  min-height: 18px;
  margin: 0;
  transition: color 0.2s;
}

.range-label--active {
  color: var(--color-text);
  font-weight: 500;
}

.preview-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.preview-heading {
  margin: 0;
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-light);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.meal-list-wrapper {
  min-height: 80px;
}

.preview-empty {
  font-size: 14px;
  font-style: italic;
  color: var(--color-text-light);
  padding: 12px 0;
  text-align: center;
  margin: 0;
}

.meal-list {
  display: flex;
  flex-direction: column;
  gap: 5px;
  max-height: 200px;
  overflow-y: auto;
}

.meal-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px !important;
}

.meal-date {
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
  min-width: 86px;
  color: var(--color-primary);
}

.meal-slot {
  font-size: 12px;
  color: var(--color-text-light);
  white-space: nowrap;
  min-width: 56px;
}

.meal-recipe {
  flex: 1;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--color-text);
}
.day-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px 4px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  min-height: 50px;
}

.day-cell:hover {
  border-color: var(--color-primary);
}

.day-name {
  font-size: 12px;
  font-weight: 700;
  margin-bottom: 2px;
  text-transform: uppercase;
}

.day-date {
  font-size: 11px;
  opacity: 0.8;
}

.day-cell[style*="background"] {
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  transform: translateY(-1px);
}
</style>