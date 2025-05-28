import { createRouter, createWebHashHistory, createWebHistory } from 'vue-router'
import LoginView from '@/views/Login.vue'
import MainView from '@/views/MainView.vue'

import { useSessionStore } from '@/stores/SessionStore'
import { Commons } from '@/networks/Commons'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'MainView',
      component: MainView,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
  ],
})

router.beforeEach((to, from, next) => {
  const session = useSessionStore()
  if (Commons.isStringBlank(session.accessToken)) {
    if (to.path === '/login') {
      next()
    } else {
      next('/login')
    }
  } else {
    if (to.path === '/login') {
      next('/')
    } else {
      next()
    }
  }
})
export default router
