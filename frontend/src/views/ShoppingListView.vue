<script setup lang="ts">
import { ref, computed, onMounted, watch } from "vue"

import AppContainer from "@/components/AppContainer.vue"
import AppSection from "@/components/AppSection.vue"
import AppCard from "@/components/AppCard.vue"
import AppInput from "@/components/AppInput.vue"
import AppButton from "@/components/AppButton.vue"
import AppCheckbox from "@/components/AppCheckbox.vue"
import AppText from "@/components/AppText.vue"
import AppDialog from "@/components/AppDialog.vue"
import IngredientSearch from "@/components/IngredientSearch.vue"
import type { IngredientResult } from "@/components/IngredientSearch.vue"

import { apiFetch } from "@/utilities/apiFetch"

interface Group {
  id: string
  name: string
}

interface ShoppingItem {
  id: string
  ingredientId: string
  ingredientName: string
  amount: number
  unit: string | null
  isBought: boolean
}

// Groups
const groups = ref<Group[]>([])
const activeGroupId = ref<string | null>(localStorage.getItem("activeGroupId"))

async function loadGroups() {
  try {
    groups.value = await apiFetch<Group[]>("/api/group/me")
    if (activeGroupId.value && !groups.value.find(g => g.id === activeGroupId.value)) {
      activeGroupId.value = groups.value[0]?.id ?? null
    }
    if (!activeGroupId.value && groups.value.length > 0) {
      activeGroupId.value = groups.value[0].id
    }
  } catch (e) {
    console.error(e)
  }
}

function onGroupChange() {
  if (activeGroupId.value) localStorage.setItem("activeGroupId", activeGroupId.value)
}

// Items
const items = ref<ShoppingItem[]>([])
const loading = ref(false)

async function loadItems() {
  if (!activeGroupId.value) return
  loading.value = true
  try {
    items.value = await apiFetch<ShoppingItem[]>(`/api/shopping-list/${activeGroupId.value}`)
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadGroups()
  await loadItems()
})

watch(activeGroupId, () => {
  onGroupChange()
  loadItems()
})

const remainingCount = computed(() => items.value.filter(i => !i.isBought).length)

function formatItem(item: ShoppingItem): string {
  const amt = item.amount % 1 === 0 ? item.amount.toFixed(0) : item.amount.toString()
  return item.unit ? `${item.ingredientName}, ${amt} ${item.unit}` : `${item.ingredientName}, ${amt}`
}

// Toggle / delete / clear
async function toggleBought(item: ShoppingItem) {
  try {
    const updated = await apiFetch<ShoppingItem>(`/api/shopping-list/item/${item.id}/toggle`, "PATCH")
    const index = items.value.findIndex(i => i.id === item.id)
    if (index !== -1) items.value[index] = updated
  } catch (e) { console.error(e) }
}

async function deleteItem(id: string) {
  try {
    await apiFetch(`/api/shopping-list/item/${id}`, "DELETE")
    items.value = items.value.filter(i => i.id !== id)
  } catch (e) { console.error(e) }
}

async function removeBought() {
  if (!activeGroupId.value) return
  try {
    await apiFetch(`/api/shopping-list/${activeGroupId.value}/bought`, "DELETE")
    items.value = items.value.filter(i => !i.isBought)
  } catch (e) { console.error(e) }
}

async function clearList() {
  if (!activeGroupId.value) return
  try {
    await apiFetch(`/api/shopping-list/${activeGroupId.value}/clear`, "DELETE")
    items.value = []
  } catch (e) { console.error(e) }
}

// Export
function exportList() {
  const lines = ["## Shopping list", ""]
  for (const item of items.value) {
    const check = item.isBought ? "[x]" : "[ ]"
    lines.push(`- ${check} ${formatItem(item)}`)
  }
  const blob = new Blob([lines.join("\n")], { type: "text/plain" })
  const url = URL.createObjectURL(blob)
  const a = document.createElement("a")
  a.href = url
  a.download = "shopping-list.txt"
  a.click()
  URL.revokeObjectURL(url)
}

// Add item dialog
const showAddDialog = ref(false)
const selectedIngredient = ref<IngredientResult | null>(null)
const newAmount = ref<number | "">("")
const newUnit = ref("")
const addError = ref("")

function openAddDialog() {
  selectedIngredient.value = null
  newAmount.value = ""
  newUnit.value = ""
  addError.value = ""
  showAddDialog.value = true
}

async function submitAddItem() {
  addError.value = ""

  if (!selectedIngredient.value) { addError.value = "Please select or type an ingredient."; return }
  if (!newAmount.value || Number(newAmount.value) <= 0) { addError.value = "Please enter a valid amount."; return }
  if (!activeGroupId.value) { addError.value = "No active group selected."; return }

  try {
    const item = await apiFetch<ShoppingItem, { ingredientName: string; amount: number; unit: string }>(
      `/api/shopping-list/${activeGroupId.value}`,
      "POST",
      {
        ingredientName: selectedIngredient.value.ingredientName,
        amount: Number(newAmount.value),
        unit: newUnit.value.trim()
      }
    )
    const existing = items.value.findIndex(i => i.id === item.id)
    if (existing !== -1) items.value[existing] = item
    else items.value.push(item)
    showAddDialog.value = false
  } catch (e) {
    addError.value = e instanceof Error ? e.message : "Failed to add item."
  }
}

// Not-implemented dialogs
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
        <div v-if="groups.length === 0" class="empty">You don't belong to any groups yet.</div>
        <div v-else class="group-selector">
          <label class="field-label" for="group-select">Active group</label>
          <select id="group-select" v-model="activeGroupId" class="group-select">
            <option v-for="g in groups" :key="g.id" :value="g.id">{{ g.name }}</option>
          </select>
        </div>
      </AppCard>
    </AppSection>

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
        <div v-if="!activeGroupId" class="empty">Select a group to see its shopping list.</div>
        <div v-else-if="loading" class="empty">Loading…</div>
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

    <!-- Add item dialog -->
    <AppDialog v-model="showAddDialog" title="Add ingredient">
      <div class="dialog-form">
        <IngredientSearch
          v-model="selectedIngredient"
          label="Ingredient"
          placeholder="Search e.g. mayonnaise, milk, sugar…"
        />

        <div class="amount-row">
          <AppInput
            v-model="newAmount"
            label="Amount"
            type="number"
            placeholder="e.g. 500"
          />
          <AppInput
            v-model="newUnit"
            label="Unit (optional)"
            placeholder="e.g. grams, loafs"
          />
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

.group-selector { display: flex; flex-direction: column; gap: 6px; }

.field-label { font-size: 14px; font-weight: 500; color: var(--color-secondary); }

.group-select {
  padding: 10px 14px;
  border-radius: 8px;
  border: 2px solid var(--color-primary-light);
  font-size: 14px;
  background: white;
  cursor: pointer;
  max-width: 300px;
  outline: none;
}

.group-select:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(243, 114, 44, 0.2);
}

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

.error-text { color: #c0392b; font-size: 0.875rem; margin: 0; }
</style>
