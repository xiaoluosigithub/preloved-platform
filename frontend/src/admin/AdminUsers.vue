<template>
  <div class="admin-page">
    <div class="tools">
      <el-input v-model="keyword" placeholder="搜索用户名/昵称" style="width:240px" />
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <el-table :data="list" style="width:100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="role" label="角色" width="120" />
      <el-table-column label="操作" width="220">
        <template #default="{row}">
          <el-button size="small" type="warning" @click="setRole(row.id, 'USER')">设为USER</el-button>
          <el-button size="small" type="success" @click="setRole(row.id, 'ADMIN')">设为ADMIN</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pager">
      <el-pagination :current-page="page" :page-size="size" :total="total" layout="prev, pager, next" @current-change="onPage" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api'
import { useRouter } from 'vue-router'

const router = useRouter()
const keyword = ref('')
const list = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

const ensureAdmin = async () => {
  const token = localStorage.getItem('token')
  if (!token) { router.push('/admin/login'); return false }
  const { data } = await api.get('/admin/ping')
  if (data.code !== 0) { router.push('/admin/login'); return false }
  return true
}

const load = async () => {
  const ok = await ensureAdmin(); if (!ok) return
  const { data } = await api.get('/admin/users', { params: { page: page.value, size: size.value, keyword: keyword.value } })
  if (data.code !== 0) return
  list.value = data.data.list || []
  total.value = data.data.total || 0
}

const onPage = (p) => { page.value = p; load() }

const setRole = async (id, role) => {
  const { data } = await api.patch(`/admin/users/${id}/role`, { role })
  if (data.code === 0) load()
}

onMounted(load)
</script>

<style scoped>
.admin-page { padding: 16px; }
.tools { display:flex; gap:8px; align-items:center; margin-bottom:12px; }
.pager { margin-top: 12px; display:flex; justify-content:center; }
</style>
