<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AppButton from '@/components/AppButton.vue'
import AppInput from '@/components/AppInput.vue'
import AppCard from '@/components/AppCard.vue'
import { register } from '@/services/authService'

const router = useRouter()

const name = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const errorMessage = ref('')
const loading = ref(false)

async function handleSubmit() {
  errorMessage.value = ''

  if (!name.value || !email.value || !password.value || !confirmPassword.value) {
    errorMessage.value = 'Please fill in all fields.'
    return
  }

  if (password.value !== confirmPassword.value) {
    errorMessage.value = 'Passwords do not match.'
    return
  }

  if (password.value.length < 8) {
    errorMessage.value = 'Password must be at least 8 characters.'
    return
  }

  loading.value = true
  try {
    await register(name.value, email.value, password.value)
    await router.push('/')
  } catch (err: unknown) {
    errorMessage.value = err instanceof Error ? err.message : 'Registration failed.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-wrapper">
      <AppCard>
        <div class="auth-header">
          <h1 class="auth-title">Create account</h1>
          <p class="auth-subtitle">Get started today</p>
        </div>

        <div class="auth-form">
          <div class="field">
            <label class="field-label" for="name">Name</label>
            <AppInput
                id="name"
                v-model="name"
                type="text"
                placeholder="Your name"
            />
          </div>

          <div class="field">
            <label class="field-label" for="email">Email</label>
            <AppInput
                id="email"
                v-model="email"
                type="email"
                placeholder="you@example.com"
            />
          </div>

          <div class="field">
            <label class="field-label" for="password">Password</label>
            <AppInput
                id="password"
                v-model="password"
                type="password"
                placeholder="At least 8 characters"
            />
          </div>

          <div class="field">
            <label class="field-label" for="confirmPassword">Confirm password</label>
            <AppInput
                id="confirmPassword"
                v-model="confirmPassword"
                type="password"
                placeholder="Repeat your password"
                @keyup.enter="handleSubmit"
            />
          </div>

          <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>
        </div>

        <template #footer>
          <div class="auth-footer">
            <AppButton
              variant="primary"
              :disabled="loading"
              @click="handleSubmit"
            >
              {{ loading ? 'Creating account…' : 'Create account' }}
            </AppButton>

            <p class="switch-text">
              Already have an account?
              <RouterLink to="/sign-in" class="switch-link">Sign in</RouterLink>
            </p>
          </div>
        </template>
      </AppCard>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--color-background, #f5f5f5);
  padding: 24px;
}

.auth-wrapper {
  width: 100%;
  max-width: 420px;
}

.auth-header {
  margin-bottom: 24px;
}

.auth-title {
  margin: 0 0 6px;
  font-size: 1.6rem;
  font-weight: 700;
}

.auth-subtitle {
  margin: 0;
  color: var(--color-text-light, #666);
  font-size: 0.95rem;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--color-text-light, #222);
}

.error-text {
  margin: 4px 0 0;
  color: #c0392b;
  font-size: 0.875rem;
}

.auth-footer {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-top: 8px;
}

.switch-text {
  margin: 0;
  text-align: center;
  font-size: 0.875rem;
  color: var(--color-text-light, #666);
}

.switch-link {
  color: var(--color-primary, #2563eb);
  font-weight: 600;
  text-decoration: none;
}

.switch-link:hover {
  text-decoration: underline;
}
</style>
