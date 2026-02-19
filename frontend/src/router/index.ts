import { createRouter, createWebHistory } from 'vue-router'

import ComponentPreview from '@/views/ComponentPreview.vue'
import CalendarView from '@/views/CalendarView.vue'
import ShoppingListView from '@/views/ShoppingListView.vue'


const routes = [

  {
    path: '/',
    component: ComponentPreview
  },

  {
    path: '/calendar',
    component: CalendarView
  },

  {
    path: '/shopping',
    component: ShoppingListView
  }

]

export default createRouter({
  history: createWebHistory(),
  routes
})
