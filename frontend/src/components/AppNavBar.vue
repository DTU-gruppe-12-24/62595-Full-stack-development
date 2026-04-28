<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { isAuthenticated, logout } from '@/services/authService'
import { showSuccess } from '@/utilities/notifications';
import AppButton from '@/components/AppButton.vue'
import UserProfileDialog from '@/views/UserProfileDialog.vue'

const router = useRouter()
const route = useRoute()

const loggedIn = ref(isAuthenticated())
const showProfile = ref(false)
const currentUserName = ref("User")

watch(() => route.path, () => {
  loggedIn.value = isAuthenticated()
})

function onLogout() {
  loggedIn.value = false
  showSuccess("Logged out successfully.")
  router.push('/sign-in')
}

</script>

<template>
  <nav class="navbar">
    <div class="navbar-container">
      <div class="logo">
        <RouterLink to="/" class="menu-item">food plan</RouterLink>
      </div>

      <!-- Menu links -->
      <div class="menu">
        <!-- Show these links only if the user is logged in -->
        <template v-if="loggedIn">

          <router-link to="/recipes" class="menu-item">
            Recipes
          </router-link>

          <RouterLink to="/calendar" class="menu-item">
            Calendar
          </RouterLink>


          <RouterLink to="/shopping" class="menu-item">
            Shopping List
          </RouterLink>

          <RouterLink to="/groups" class="menu-item">
            Groups
          </RouterLink>

          <RouterLink to="/statistics" class="menu-item">
            My statistics
          </RouterLink>

          <AppButton variant="ghost" class="user-pill" @click="showProfile = true">
            <span class="icon">👤</span>
            <span>Account</span>
          </AppButton>
        </template>
        <!-- Show these links only if the user is not logged in -->
        <template v-else>
          <RouterLink to="/sign-in" class="menu-item">
            Sign in
          </RouterLink>
        </template>
      </div>
    </div>
  </nav>

  <UserProfileDialog
      v-model="showProfile"
      @logged-out="onLogout"
  />
</template>
<style scoped>

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 64px;
  background: var(--color-primary);
  color: white;
  display: flex;
  align-items: center;
  padding: 0 var(--space-lg);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  z-index: 1000;
}

.navbar-container {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.menu {
  display: flex;
  align-items: center;
  gap: var(--space-lg);
}

.menu-item {
  color: white;
  text-decoration: none;
  font-weight: 500;
}


.icon {
  font-size: 20px;
}

.user-pill {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  background: rgba(255, 255, 255, 0.2);
  border: none;
  padding: 6px 16px;
  border-radius: 20px;
  color: white;
  cursor: pointer;
  font-weight: 500;
}
</style>
