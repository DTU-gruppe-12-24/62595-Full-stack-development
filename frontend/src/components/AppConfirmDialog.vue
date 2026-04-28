<script setup lang="ts">
import AppDialog from '@/components/AppDialog.vue'
import AppButton from '@/components/AppButton.vue'
import AppText from '@/components/AppText.vue'

withDefaults(defineProps<{
  modelValue: boolean
  title: string
  message: string
  confirmLabel?: string
  cancelLabel?: string
  confirmVariant?: 'primary' | 'secondary' | 'ghost' | 'cancel' | 'danger'
  busy?: boolean
}>(), {
  confirmLabel: 'Confirm',
  cancelLabel: 'Cancel',
  confirmVariant: 'danger',
  busy: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  confirm: []
}>()

function close() {
  emit('update:modelValue', false)
}

function onConfirm() {
  emit('confirm')
}
</script>

<template>
  <AppDialog :model-value="modelValue" :title="title" @update:model-value="emit('update:modelValue', $event)">
    <AppText>{{ message }}</AppText>
    <template #footer>
      <AppButton variant="cancel" @click="close">{{ cancelLabel }}</AppButton>
      <AppButton :variant="confirmVariant" :disabled="busy" @click="onConfirm">{{ confirmLabel }}</AppButton>
    </template>
  </AppDialog>
</template>
