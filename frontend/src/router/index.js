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
import OrderBuy from '@/views/OrderBuy.vue'
import OrderListBuy from '@/views/OrderListBuy.vue'
import OrderListSell from '@/views/OrderListSell.vue'
import AdminLogin from '@/admin/AdminLogin.vue'
import AdminLayout from '@/admin/AdminLayout.vue'
import AdminProducts from '@/admin/AdminProducts.vue'
import AdminUsers from '@/admin/AdminUsers.vue'
import AdminCategories from '@/admin/AdminCategories.vue'
import AdminStats from '@/admin/AdminStats.vue'

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
      { path: 'like', component: LikeList, meta: { requiresAuth: true } },
      { path: 'order-buy', component: OrderListBuy, meta: { requiresAuth: true } },
      { path: 'order-sell', component: OrderListSell, meta: { requiresAuth: true } }
    ]
  }
  ,{ path: '/order/buy/:productId', component: OrderBuy, meta: { requiresAuth: true } }
  ,{ path: '/order/list/buy', redirect: '/profile/order-buy' }
  ,{ path: '/order/list/sell', redirect: '/profile/order-sell' }
  ,{ path: '/admin/login', component: AdminLogin }
  ,{ path: '/admin', component: AdminLayout, children: [
    { path: '', redirect: '/admin/products' },
    { path: 'products', component: AdminProducts },
    { path: 'users', component: AdminUsers },
    { path: 'categories', component: AdminCategories },
    { path: 'stats', component: AdminStats }
  ]}
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) return next('/login')
  if (to.path.startsWith('/admin') && to.path !== '/admin/login') {
    const u = localStorage.getItem('user')
    const user = u ? JSON.parse(u) : null
    if (!token || !user || user.role !== 'ADMIN') return next('/admin/login')
  }
  next()
})

export default router