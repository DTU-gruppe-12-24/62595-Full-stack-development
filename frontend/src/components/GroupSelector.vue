<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import AppDropdown from '@/components/AppDropdown.vue'
import { apiFetch } from '@/utilities/apiFetch'

export interface Group {
  id: string
  name: string
}

const STORAGE_KEY = 'activeGroupId'

const props = defineProps<{
  modelValue: Group | null
  persist?: boolean
  allowNull?: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: Group | null]
}>()

const groups = ref<Group[]>([])
const selectedName = ref<string | undefined>(undefined)
const loading = ref(true)

const groupNames = computed(() => (props.allowNull ? ["None"] : []).concat(groups.value.map(g => g.name)))

interface GroupMemberResponse {
  group: Group
  role: string
}

onMounted(async () => {
  try {
    const memberships = await apiFetch<GroupMemberResponse[]>('/api/group/me')
    groups.value = memberships.map(m => m.group)

    if (props.modelValue == null) {
        const savedId = localStorage.getItem(STORAGE_KEY)
        const savedGroup = props.persist ? (savedId ? groups.value.find(g => g.id === savedId) ?? null : null) : null;
        const initial = savedGroup ?? (props.allowNull ? null : groups.value[0]) ?? null

        if (initial) selectedName.value = initial.name
        else if (props.allowNull) selectedName.value = "None"
        persistAndEmit(initial)
    } else {
        selectedName.value = props.modelValue.name
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})

function onChange(name: string) {
  selectedName.value = name
  const group = groups.value.find(g => g.name === name) ?? null
  persistAndEmit(group)
}

function persistAndEmit(group: Group | null) {
    if (props.persist)
        if (group) {
            localStorage.setItem(STORAGE_KEY, group.id)
        } else {
            localStorage.removeItem(STORAGE_KEY)
        }
  emit('update:modelValue', group)
}
</script>

<template>
  <p v-if="!loading && groups.length === 0" class="empty">
    You don't belong to any groups yet.
  </p>

  <AppDropdown
    v-else
    :model-value="selectedName"
    :values="groupNames"
    :disabled="loading"
    label="Active group"
    @update:model-value="onChange"
  />
</template>

<style scoped>
.empty {
  font-size: 14px;
  color: #888;
  margin: 0;
  font-style: italic;
}
</style>
