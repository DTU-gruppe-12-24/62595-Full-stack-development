import { createRouter, createWebHistory } from 'vue-router'


const routes = [

  {
    path: '/',
    component: () => import('@/views/ComponentPreview.vue')
  },

  {
    path: '/calendar',
    component: () => import('@/views/CalendarView.vue')
  },

  {
    path: '/shopping',
    component: () => import('@/views/ShoppingListView.vue')
  },

  {
    path: '/groups',
    component: () => import('@/views/GroupView.vue'),
  },

  {
    path: "/recipes/create",
    component: () => import("../views/CreateRecipeView.vue")
  },

  {
    path: "/recipes/:id/edit",
    component: () => import("../views/EditRecipeView.vue")
  }

]

export default createRouter({
  history: createWebHistory(),
  routes
})
