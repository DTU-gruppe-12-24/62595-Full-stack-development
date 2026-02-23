<script lang="ts">
import {defineComponent} from 'vue'


export default defineComponent({
  name: "AppNavBar"
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
        <RouterLink to="/calendar" class="menu-item">
          Calendar
        </RouterLink>

        <RouterLink to="/shopping" class="menu-item">
          Shopping List
        </RouterLink>

        <RouterLink to="/groups" class="menu-item">
          Groups
        </RouterLink>
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
