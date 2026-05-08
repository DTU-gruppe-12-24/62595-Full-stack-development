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
const menuOpen = ref(false)

watch(() => route.path, () => {
  loggedIn.value = isAuthenticated()
  menuOpen.value = false
})

function onLogout() {
  loggedIn.value = false
  menuOpen.value = false
  showSuccess("Logged out successfully.")
  router.push('/sign-in')
}

function closeMenu() {
  menuOpen.value = false
}
</script>

<template>
  <nav class="navbar">
    <div class="navbar-container">
      <div class="logo">
        <RouterLink to="/" class="menu-item" @click="closeMenu">OurMenu</RouterLink>
      </div>

      <!-- Desktop menu links -->
      <div class="menu desktop-menu">
        <template v-if="loggedIn">
          <router-link to="/recipes" class="menu-item">Recipes</router-link>
          <RouterLink to="/calendar" class="menu-item">Calendar</RouterLink>
          <RouterLink to="/shopping" class="menu-item">Shopping List</RouterLink>
          <RouterLink to="/groups" class="menu-item">Groups</RouterLink>
          <RouterLink to="/statistics" class="menu-item">My statistics</RouterLink>
          <AppButton variant="ghost" class="user-pill" @click="showProfile = true">
            <span class="icon">👨‍🍳</span>
            <span>Account</span>
          </AppButton>
        </template>
        <template v-else>
          <RouterLink to="/sign-in" class="menu-item">Sign in</RouterLink>
        </template>
      </div>

      <!-- Hamburger button (mobile only) -->
      <button class="hamburger" :class="{ open: menuOpen }" @click="menuOpen = !menuOpen" aria-label="Toggle menu">
        <span></span>
        <span></span>
        <span></span>
      </button>
    </div>

    <!-- Mobile dropdown menu -->
    <div v-if="menuOpen" class="mobile-menu">
      <template v-if="loggedIn">
        <RouterLink to="/recipes" class="mobile-menu-item" @click="closeMenu">Recipes</RouterLink>
        <RouterLink to="/calendar" class="mobile-menu-item" @click="closeMenu">Calendar</RouterLink>
        <RouterLink to="/shopping" class="mobile-menu-item" @click="closeMenu">Shopping List</RouterLink>
        <RouterLink to="/groups" class="mobile-menu-item" @click="closeMenu">Groups</RouterLink>
        <RouterLink to="/statistics" class="mobile-menu-item" @click="closeMenu">My statistics</RouterLink>
        <button class="mobile-menu-item mobile-account-btn" @click="() => { showProfile = true; closeMenu() }">
          <span class="icon">👨‍🍳</span> Account
        </button>
      </template>
      <template v-else>
        <RouterLink to="/sign-in" class="mobile-menu-item" @click="closeMenu">Sign in</RouterLink>
      </template>
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
  background: var(--color-primary);
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  z-index: 1000;
}

.navbar-container {
  height: 64px;
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 var(--space-lg);
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

/* Hamburger button */
.hamburger {
  display: none;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 5px;
  width: 40px;
  height: 40px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  border-radius: var(--radius-sm);
}

.hamburger span {
  display: block;
  width: 22px;
  height: 2px;
  background: white;
  border-radius: 2px;
  transition: transform 0.2s, opacity 0.2s;
}

.hamburger.open span:nth-child(1) {
  transform: translateY(7px) rotate(45deg);
}

.hamburger.open span:nth-child(2) {
  opacity: 0;
}

.hamburger.open span:nth-child(3) {
  transform: translateY(-7px) rotate(-45deg);
}

/* Mobile dropdown */
.mobile-menu {
  display: flex;
  flex-direction: column;
  background: var(--color-primary);
  border-top: 1px solid rgba(255, 255, 255, 0.15);
  padding: var(--space-sm) 0;
}

.mobile-menu-item {
  color: white;
  text-decoration: none;
  font-weight: 500;
  padding: 14px var(--space-lg);
  display: block;
  transition: background 0.15s;
}

.mobile-menu-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.mobile-account-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-family: inherit;
  font-size: inherit;
  text-align: left;
  width: 100%;
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

@media (max-width: 768px) {
  .desktop-menu {
    display: none;
  }

  .hamburger {
    display: flex;
  }
}
</style>
