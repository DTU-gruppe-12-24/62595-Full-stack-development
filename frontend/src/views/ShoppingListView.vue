<script setup lang="ts">
import { ref, computed } from "vue"

import AppContainer from "@/components/AppContainer.vue"
import AppSection from "@/components/AppSection.vue"
import AppCard from "@/components/AppCard.vue"
import AppInput from "@/components/AppInput.vue"
import AppButton from "@/components/AppButton.vue"
import AppCheckbox from "@/components/AppCheckbox.vue"
import AppText from "@/components/AppText.vue"

interface ShoppingItem {
  id: number
  name: string
  checked: boolean
}

const items = ref<ShoppingItem[]>([])
const newItem = ref("")

function addItem() {
  if (!newItem.value.trim()) return

  items.value.push({
    id: Date.now(),
    name: newItem.value,
    checked: false
  })

  newItem.value = ""
}

function removeItem(id: number) {
  items.value = items.value.filter(i => i.id !== id)
}

const remainingCount = computed(
    () => items.value.filter(i => !i.checked).length
)
</script>

<template>
  <AppContainer>
    <AppText variant="title" class="page-title">
      Shopping List
    </AppText>

    <AppSection>
      <template #title>Add Ingredient</template>

      <AppCard flex class="add-card">
        <AppInput
            v-model="newItem"
            placeholder="Milk, Tomatoes, Pasta..."
            class="grow"
            @keyup.enter="addItem"
        />

        <AppButton variant="primary" @click="addItem">
          Add
        </AppButton>
      </AppCard>
    </AppSection>

    <AppSection>
      <template #title>
        Items ({{ remainingCount }} left)
      </template>

      <AppCard class="list-card">
        <div v-if="items.length === 0" class="empty">
          No items added yet
        </div>

        <div
            v-for="item in items"
            :key="item.id"
            class="list-item"
        >
          <AppCheckbox
              v-model="item.checked"
              :label="item.name"
              :class="{ 'item-checked': item.checked }"
          />

          <button
              class="delete-btn"
              @click="removeItem(item.id)"
          >
            ✕
          </button>
        </div>
      </AppCard>
    </AppSection>
  </AppContainer>
</template>

<style scoped>
.page-title {
  margin-bottom: 24px;
}

.grow {
  flex: 1;
}

.list-card {
  margin-top: 10px;
}

.list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f9c74f20;
}

.list-item:last-child {
  border-bottom: none;
}



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

.delete-btn:hover {
  background: #fff5f0;
}

.empty {
  color: #888;
  text-align: center;
  padding: 30px;
  font-style: italic;
}
</style>