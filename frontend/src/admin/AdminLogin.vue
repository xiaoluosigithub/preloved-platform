<template>
  <div class="admin-login">
    <el-card class="box">
      <h2>管理员登录</h2>
      <el-form :model="form" @submit.prevent="onSubmit">
        <el-form-item label="用户名">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" autocomplete="current-password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit" :loading="loading">登录</el-button>
        </el-form-item>
      </el-form>
      <el-alert v-if="msg" :title="msg" type="error" show-icon />
    </el-card>
  </div>
  </template>

<script setup>
import { ref } from 'vue'
import api from '@/api'
import { useRouter } from 'vue-router'

const router = useRouter()
const form = ref({ username: '', password: '' })
const loading = ref(false)
const msg = ref('')

const onSubmit = async () => {
  msg.value = ''
  if (!form.value.username || !form.value.password) { msg.value = '请输入用户名和密码'; return }
  loading.value = true
  try {
    const { data } = await api.post('/auth/login', form.value)
    if (data.code !== 0) { msg.value = data.msg || '登录失败'; return }
    const { token, user } = data.data
    if (!user || user.role !== 'ADMIN') { msg.value = '非管理员账户'; return }
    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify(user))
    router.push('/admin/products')
  } catch (e) {
    msg.value = '网络错误'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.admin-login { display: flex; justify-content: center; align-items: center; min-height: 60vh; }
.box { width: 360px; }
</style>