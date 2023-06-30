import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Login',
      component: () => import('../views/LoginPage.vue')
    },
    {
      path: '/dashboard',
      name: 'Manager - Dashboard',
      component: () => import('../views/Dashboard.vue'),
      children: [
        {
          path: 'category',
          component: () => import('../components/manager/CategoryEditor/CategoryEditor.vue')
        },
        {
          path: 'product',
          component: () => import('../components/manager/ProductEditor/ProductEditor.vue')
        }
      ]
    }
  ]
})

export default router
