import { createRouter, createWebHashHistory, createWebHistory } from 'vue-router'
import LoginView from '@/views/Login.vue'
import MainView from '@/views/MainView.vue'
import AdminMainView from '@/views/admin/AdminMainView.vue'

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
    {
      path: '/admin',
      name: 'admin',
      component: AdminMainView,
    },
  ],
})

router.beforeEach((to, from, next) => {
  const session = useSessionStore()
  if (Commons.isStringBlank(session.accessToken)) {
    // 尚未登录
    if (to.path === '/login') {
      next()
    } else {
      next('/login')
    }
  } else {
    // 已经登录了
    if (to.path === '/login') {
      next('/')
    } else {
      next()
    }
  }
})
export default router
