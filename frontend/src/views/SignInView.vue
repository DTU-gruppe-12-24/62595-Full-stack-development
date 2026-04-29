<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AppButton from '@/components/AppButton.vue'
import AppInput from '@/components/AppInput.vue'
import AppCard from '@/components/AppCard.vue'
import { login } from '@/services/authService'
import { showError, showSuccess } from '@/utilities/notifications'

const router = useRouter()

const email = ref('')
const password = ref('')
const loading = ref(false)

async function handleSubmit() {

  if (!email.value || !password.value)
      return showError('Please fill in all fields.');

  loading.value = true
  try {
      await login(email.value, password.value)
      showSuccess('Logged in successfully.')
      await router.push('/')
  } catch (err: unknown) {
      showError(err instanceof Error ? err.message : 'Login failed.');
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
          <h1 class="auth-title">Welcome back</h1>
          <p class="auth-subtitle">Sign in to your account</p>
        </div>

        <div class="auth-form">
          <form @submit.prevent="handleSubmit">
            <div class="field">

              <label class="field-label" for="email">Email</label>
              <AppInput
                id="email"
                v-model="email"
                type="email"
                placeholder="you@example.com"
                autocomplete="email"
              />
            </div>

            <div class="field">
              <label class="field-label" for="password">Password</label>
              <AppInput
                id="password"
                v-model="password"
                type="password"
                placeholder="Your password"
                autocomplete="current-password"
              />
            </div>
          </form>
        </div>

        <template #footer>
          <div class="auth-footer">
            <AppButton
              variant="primary"
              :disabled="loading"
              @click="handleSubmit"
            >
              {{ loading ? 'Signing in…' : 'Sign in' }}
            </AppButton>

            <p class="switch-text">
              Don't have an account?
              <RouterLink to="/sign-up" class="switch-link">Sign up</RouterLink>
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
  color: var(--color-text, #666);
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
