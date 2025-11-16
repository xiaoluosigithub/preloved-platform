<template>
  <div class="admin-page">
    <div class="tools">
      <el-input v-model="name" placeholder="分类名" style="width:240px" />
      <el-button type="primary" @click="add">新增</el-button>
    </div>
    <el-table :data="list" style="width:100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column label="操作" width="160">
        <template #default="{row}">
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const list = ref([])
const name = ref('')

const ensureAdmin = async () => {
  const token = localStorage.getItem('token')
  if (!token) { router.push('/admin/login'); return false }
  const { data } = await api.get('/admin/ping')
  if (data.code !== 0) { router.push('/admin/login'); return false }
  return true
}

const load = async () => {
  const ok = await ensureAdmin(); if (!ok) return
  const { data } = await api.get('/admin/categories')
  if (data.code !== 0) return
  list.value = data.data || []
}

const add = async () => {
  if (!name.value) return
  const { data } = await api.post('/admin/categories', { name: name.value })
  if (data.code === 0) { name.value = ''; load() }
}

const remove = async (id) => {
  await ElMessageBox.confirm('确认删除该分类？')
  const { data } = await api.delete(`/admin/categories/${id}`)
  if (data.code === 0) load()
}

onMounted(load)
</script>

<style scoped>
.admin-page { padding: 16px; }
.tools { display:flex; gap:8px; align-items:center; margin-bottom:12px; }
</style>