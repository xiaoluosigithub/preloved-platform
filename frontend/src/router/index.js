import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import ProductPublish from '@/views/ProductPublish.vue'
import ProductDetail from '@/views/ProductDetail.vue'
import Blank from '@/views/Blank.vue'

const routes = [
  { path: '/', component: Home, meta: { requiresAuth: false } },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/publish', component: ProductPublish, meta: { requiresAuth: true } },
  { path: '/product/:id', component: ProductDetail, props: true }
  ,{ path: '/hybridaction/:rest(.*)', component: Blank }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) next('/login')
  else next()
})

export default router