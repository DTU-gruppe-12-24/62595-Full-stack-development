<script setup lang="ts">
import { ref, watch } from 'vue'
import AppButton from "@/components/AppButton.vue"
import AppInput from "@/components/AppInput.vue"
import AppDialog from "@/components/AppDialog.vue"
import AppConfirmDialog from '@/components/AppConfirmDialog.vue'
import { apiFetch } from "@/utilities/apiFetch"
import { logout } from "@/services/authService"
import { showError, showSuccess } from '@/utilities/notifications'

const props = defineProps<{ modelValue: boolean }>()
const emit = defineEmits(['update:modelValue', 'logged-out'])

const user = ref({ name: '', email: '' })
const isEditing = ref(false)
const editType = ref<'name' | 'email' | 'password'>('name')

const newValue = ref("")
const currentPassword = ref("")
const isLoading = ref(false)
const showDeleteConfirm = ref(false)

async function fetchUser() {
  try {
    user.value = await apiFetch("/api/users/me", "GET")
  } catch (e) {
    showError(e instanceof Error ? e.message : 'Failed to fetch user data')
  }
}

watch(() => props.modelValue, (val) => {
  if (val) fetchUser()
})

async function performClientLogout() {
  await logout()
  emit('update:modelValue', false)
  emit('logged-out')
}

function openEdit(type: 'name' | 'email' | 'password') {
  editType.value = type
  newValue.value = type === 'password' ? '' : user.value[type as 'name' | 'email']
  currentPassword.value = ""
  isEditing.value = true
}

async function handleConfirmEdit() {
  if (editType.value === 'password' && newValue.value.length < 8) {
    return showError('New password must be at least 8 characters.')
  }

  isLoading.value = true
  try {
    const payload: any = { currentPassword: currentPassword.value }
    if (editType.value === 'password') payload.newPassword = newValue.value
    else payload[editType.value] = newValue.value

    const response = await apiFetch<any>("/api/users/me", "PUT", payload)

    // Update token if one is returned (important for password/email changes)
    if (response && response.token) {
      localStorage.setItem("token", response.token)
    }

    await fetchUser()
    isEditing.value = false
    showSuccess('Changes saved!')
  } catch (e: any) {
    showError(e.message || 'Update failed. Please verify your current password.')
  } finally {
    isLoading.value = false
  }
}

async function handleDelete() {
  try {
    isLoading.value = true
    await apiFetch("/api/users/me", "DELETE")
    showSuccess('Account deleted successfully.')
    await performClientLogout()
  } catch (e: any) {
    showError(e.message || 'Error deleting account')
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <AppDialog :model-value="modelValue" @update:model-value="emit('update:modelValue', $event)" title="My Account">
    <div class="profile-layout">

      <div class="info-grid">
        <div class="info-row">
          <div class="label-group">
            <span class="label">Display Name</span>
            <span class="value">{{ user.name }}</span>
          </div>
          <AppButton variant="secondary" @click="openEdit('name')">Edit</AppButton>
        </div>

        <div class="info-row">
          <div class="label-group">
            <span class="label">Email Address</span>
            <span class="value">{{ user.email }}</span>
          </div>
          <AppButton variant="secondary" @click="openEdit('email')">Edit</AppButton>
        </div>

        <div class="info-row">
          <div class="label-group">
            <span class="label">Password</span>
            <span class="value">********</span>
          </div>
          <AppButton variant="secondary" @click="openEdit('password')">Change</AppButton>
        </div>
      </div>

      <div class="danger-zone">
        <h4>GDPR & Privacy</h4>
        <p>Removing your data is permanent and deletes all recipes you have created, and transfers ownership of any groups you created.</p>
        <AppButton variant="danger" @click="showDeleteConfirm = true">Delete My Information</AppButton>
      </div>
    </div>

    <template #footer>
      <div class="footer-between">
        <AppButton variant="secondary" @click="performClientLogout">
          Log Out
        </AppButton>
        <AppButton variant="cancel" @click="emit('update:modelValue', false)">
          Cancel
        </AppButton>
      </div>
    </template>
  </AppDialog>

  <AppDialog v-model="isEditing" :title="'Update ' + editType">
    <div class="edit-popup">
      <div class="field">
        <label>New {{ editType }}</label>
        <AppInput
            v-model="newValue"
            :type="editType === 'password' ? 'password' : 'text'"
            :placeholder="'Enter new ' + editType"
        />
        <small v-if="editType === 'password'">Minimum 8 characters</small>
      </div>

      <div class="field verification">
        <label>Confirm with Current Password</label>
        <AppInput v-model="currentPassword" type="password" placeholder="Current password..." />
      </div>
    </div>

    <template #footer>
      <AppButton variant="cancel" @click="isEditing = false">Cancel</AppButton>
      <AppButton variant="primary" :disabled="isLoading" @click="handleConfirmEdit">
        Confirm Change
      </AppButton>
    </template>
  </AppDialog>

  <AppConfirmDialog
    v-model="showDeleteConfirm"
    title="Delete your account?"
    message="This deletes your profile and associated data permanently. This action cannot be undone."
    confirm-label="Delete my account"
    confirm-variant="danger"
    :busy="isLoading"
    @confirm="handleDelete"
  />
</template>

<style scoped>
.profile-layout {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
}

.info-grid {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-md);
  background: var(--color-surface);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
}

.label-group {
  display: flex;
  flex-direction: column;
}

.label {
  font-size: 12px;
  color: var(--color-text-light);
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: var(--space-xs);
}

.value {
  font-weight: 600;
  font-size: 16px;
  color: var(--color-text);
}

.danger-zone {
  padding: var(--space-md);
  background: #fff5f5; /* Light red background */
  border-radius: var(--radius-md);
  border: 1px solid var(--color-danger);
}

.danger-zone h4 {
  color: var(--color-danger);
  margin: 0 0 var(--space-xs) 0;
}

.danger-zone p {
  font-size: 13px;
  color: var(--color-text-light);
  margin-bottom: var(--space-md);
}

.footer-between {
  width: 100%;
  display: flex;
  justify-content: space-between;
}

.edit-popup {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.field label {
  display: block;
  font-size: 14px;
  margin-bottom: var(--space-xs);
}

.verification {
  margin-top: var(--space-sm);
  padding-top: var(--space-md);
  border-top: 1px solid var(--color-border);
}

small {
  display: block;
  margin-top: var(--space-xs);
  color: var(--color-text-light);
  font-size: 12px;
}
</style>
