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
  }

]

export default createRouter({
  history: createWebHistory(),
  routes
})
