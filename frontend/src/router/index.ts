import { createRouter, createWebHistory } from 'vue-router'
import { isAuthenticated } from '@/services/authService'


// Auth views
import SignInView from '@/views/SignInView.vue'
import SignUpView from '@/views/SignUpView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/sign-in',
      name: 'sign-in',
      component: SignInView,
      meta: {public: true},
    },
    {
      path: '/sign-up',
      name: 'sign-up',
      component: SignUpView,
      meta: {public: true},
    },

    {
      path: '/',
      name: 'home',
      component: () => import('@/views/ComponentPreview.vue')
    },

    {
      path: '/calendar',
      name: 'calendar',
      component: () => import('@/views/CalendarView.vue')
    },

    {
      path: '/shopping',
      name: 'shopping',
      component: () => import('@/views/ShoppingListView.vue')
    },

    {
      path: '/groups',
      name: 'groups',
      component: () => import('@/views/GroupView.vue'),
    },
    {
      path: '/groups',
      name: 'groups',
      component: () => import('@/views/GroupView.vue'),
    },

    {
      path: "/recipes/create",
      name: 'recipes',
      component: () => import("../views/CreateRecipeView.vue")
    },

    {
      path: "/recipes/:id/edit",
      name: 'recipeEdit',
      component: () => import("../views/EditRecipesView.vue")
    },

    {
      path: "/recipes",
      name: "recipes",
      component: () => import("../views/EditRecipesView.vue")
    }
  ],
})
// Nav guard
router.beforeEach((to) => {
  const authenticated = isAuthenticated()

  // Redirect unauthenticated users to sign-in
  if (!to.meta.public && !authenticated) {
    return { name: 'sign-in' }
  }

  // Redirect logged-in users away from sign-in/sign-up
  if (to.meta.public && authenticated) {
    return { name: 'home' }
  }
})
export default router
