<template>
  <AuthLayout 
    title="创建新账户" 
    subtitle="加入我们，开始您的二手交易之旅">
    <el-form 
      :model="form" 
      @submit.prevent="onSubmit"
      class="auth-form"
      label-position="top">
      <el-form-item label="用户名" prop="username">
        <el-input 
          v-model="form.username" 
          placeholder="请输入用户名"
          size="large"
          :prefix-icon="User">
        </el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input 
          type="password" 
          v-model="form.password" 
          placeholder="请输入密码"
          size="large"
          :prefix-icon="Lock"
          show-password>
        </el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input 
          type="password" 
          v-model="form.confirmPassword" 
          placeholder="请再次输入密码"
          size="large"
          :prefix-icon="CircleCheck"
          show-password>
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button 
          type="primary" 
          @click="onSubmit"
          size="large"
          class="auth-btn"
          :loading="loading"
          block>
          注册
        </el-button>
      </el-form-item>
      <div class="auth-footer">
        <p>已有账户？</p>
        <el-button link @click="$router.push('/login')" class="link-btn">立即登录</el-button>
      </div>
    </el-form>
    <template #features>
      <div class="feature-item">
        <el-icon class="feature-icon"><Key /></el-icon>
        <span>安全可靠</span>
      </div>
      <div class="feature-item">
        <el-icon class="feature-icon"><CirclePlus /></el-icon>
        <span>快速注册</span>
      </div>
      <div class="feature-item">
        <el-icon class="feature-icon"><StarFilled /></el-icon>
        <span>新用户福利</span>
      </div>
    </template>
  </AuthLayout>
</template>

<script>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { User, Lock, CircleCheck, Key, CirclePlus, StarFilled } from '@element-plus/icons-vue'
import api from '@/api'
import AuthLayout from '@/components/auth/AuthLayout.vue'

export default {
  name: 'Register',
  components: { User, Lock, CircleCheck, Key, CirclePlus, StarFilled, AuthLayout },
  setup() {
    const router = useRouter()
    const form = ref({
      username: '',
      password: '',
      confirmPassword: ''
    })
    const loading = ref(false)

    const onSubmit = async () => {
      if (!form.value.username || !form.value.password) {
        ElMessage.warning('请填写完整信息')
        return
      }
      const pwd = form.value.password || ''
      const cpwd = form.value.confirmPassword || ''
      if (pwd !== cpwd) {
        ElMessage.warning('两次输入的密码不一致')
        return
      }

      loading.value = true
      try {
        const { username, password } = form.value
        const res = await api.post('/auth/register', { username, password })
        const { code, msg } = res.data || {}
        if (code !== undefined && code !== 0) {
          throw new Error(msg || '注册失败，请稍后重试')
        }
        ElMessage.success('注册成功，请登录')
        router.replace('/login')
      } catch (err) {
        const raw = err.response?.data?.msg || err.message || ''
        const lower = String(raw).toLowerCase()
        let display = '注册失败，请稍后重试'
        if (raw.includes('已存在') || lower.includes('exist')) display = '用户名已存在'
        ElMessage.error(display)
      } finally {
        loading.value = false
      }
    }

    return {
      form,
      loading,
      onSubmit,
      User,
      Lock,
      CircleCheck,
      Key,
      CirclePlus,
      StarFilled
    }
  }
}
</script>

<style scoped>
.auth-form { margin-bottom: 24px; }
.auth-btn { width: 100%; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none; font-weight: 600; transition: all 0.3s ease; }
.auth-btn:hover { transform: translateY(-2px); box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4); }
.auth-footer { text-align: center; display: flex; align-items: center; justify-content: center; gap: 8px; }
.auth-footer p { color: var(--text-secondary); margin: 0; }
.link-btn { color: var(--primary-color); font-weight: 600; padding: 4px 8px; }
.link-btn:hover { color: var(--info-color); }
.feature-item { display: flex; flex-direction: column; align-items: center; gap: 8px; color: rgba(255, 255, 255, 0.9); font-size: 0.9rem; }
.feature-icon { font-size: 1.5rem; color: rgba(255, 255, 255, 0.8); }
</style>