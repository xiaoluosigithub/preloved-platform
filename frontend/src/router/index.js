import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import ProductPublish from '@/views/ProductPublish.vue'
import ProductDetail from '@/views/ProductDetail.vue'
import Blank from '@/views/Blank.vue'
import Profile from '@/views/Profile.vue'
import ProfileInfo from '@/views/ProfileInfo.vue'
import MyProducts from '@/views/ProfileMyProducts.vue'
import FavoriteList from '@/views/ProfileFavorite.vue'
import LikeList from '@/views/ProfileLike.vue'

const routes = [
  { path: '/', component: Home, meta: { requiresAuth: false } },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/publish', component: ProductPublish, meta: { requiresAuth: true } },
  { path: '/product/:id', component: ProductDetail, props: true }
  ,{ path: '/hybridaction/:rest(.*)', component: Blank }
  ,{
    path: '/profile',
    component: Profile,
    meta: { requiresAuth: true },
    children: [
      { path: '', component: ProfileInfo, meta: { requiresAuth: true } },
      { path: 'my-products', component: MyProducts, meta: { requiresAuth: true } },
      { path: 'favorite', component: FavoriteList, meta: { requiresAuth: true } },
      { path: 'like', component: LikeList, meta: { requiresAuth: true } }
    ]
  }
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