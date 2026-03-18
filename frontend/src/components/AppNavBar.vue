<script lang="ts">
import { defineComponent, ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { isAuthenticated, logout } from '@/services/authService'

export default defineComponent({
  name: "AppNavBar",
  setup() {
    const router = useRouter()
    const route = useRoute()

    // Re-check auth state whenever the route changes
    const loggedIn = ref(isAuthenticated())
    watch(() => route.path, () => {
      loggedIn.value = isAuthenticated()
    })

    async function handleLogout() {
      await logout()
      loggedIn.value = false
      router.push('/sign-in')
    }

    return { loggedIn, handleLogout }
  }
})
</script>

<template>
  <nav class="navbar">
    <div class="navbar-container">

      <!-- Logo -->
      <div class="logo">
        <RouterLink to="/" class="menu-item">
          food plan
        </RouterLink>
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

          <a class="menu-item" @click.prevent="handleLogout" href="#">
            Log out
          </a>
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

  padding: 0 24px;

  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);

  z-index: 1000;
}

.navbar-container {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-weight: 600;
  font-size: 18px;
}

.menu {
  display: flex;
  gap: 24px;
}

.menu-item {
  color: white;
  text-decoration: none;
  font-weight: 500;
  transition: opacity 0.2s ease;
}

.menu-item:hover {
  opacity: 0.8;
}

.router-link-active {
  border-bottom: 2px solid var(--color-primary-light);
}

</style>
