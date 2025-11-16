<template>
  <el-container class="admin-layout">
    <el-aside width="200px" class="aside">
      <div class="brand">管理后台</div>
      <el-button class="exit-btn" type="danger" plain @click="exitAdmin">退出管理员界面</el-button>
      <el-menu :default-active="active" @select="onSelect">
        <el-menu-item index="products">商品审核</el-menu-item>
        <el-menu-item index="users">用户管理</el-menu-item>
        <el-menu-item index="categories">分类管理</el-menu-item>
        <el-menu-item index="stats">订单统计</el-menu-item>
      </el-menu>
    </el-aside>
    <el-main class="main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const active = ref('products')

const ensureAdmin = async () => {
  const token = localStorage.getItem('token')
  if (!token) { router.push('/admin/login'); return false }
  const { data } = await api.get('/admin/ping')
  if (data.code !== 0) { router.push('/admin/login'); return false }
  return true
}

onMounted(async () => {
  const ok = await ensureAdmin(); if (!ok) return
  const seg = route.path.split('/').pop()
  active.value = ['products','users','categories','stats'].includes(seg) ? seg : 'products'
})

const onSelect = (key) => { router.push(`/admin/${key}`) }

const exitAdmin = () => { router.push('/profile') }
</script>

<style scoped>
.admin-layout { height: 100%; min-height: 80vh; }
.aside { border-right: 1px solid #eee; }
.brand { padding: 16px; font-weight: 700; }
.exit-btn { width: 100%; margin: 8px 0; }
.main { padding: 0; }
</style>