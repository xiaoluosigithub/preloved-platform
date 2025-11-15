<template>
  <div class="auth-container">
    <div class="auth-wrapper">
      <div class="auth-header">
        <div class="logo-section">
          <div class="logo-icon">🛍️</div>
          <h1 class="logo-text">Secondhand</h1>
        </div>
        <h2 class="auth-title">创建新账户</h2>
        <p class="auth-subtitle">加入我们，开始您的二手交易之旅</p>
      </div>

      <el-card class="auth-card">
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
        </el-form>

        <div class="auth-footer">
          <p>已有账户？</p>
          <el-button 
            type="text" 
            @click="$router.push('/login')"
            class="link-btn">
            立即登录
          </el-button>
        </div>
      </el-card>

      <div class="auth-features">
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
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, CircleCheck, Key, CirclePlus, StarFilled } from '@element-plus/icons-vue'
import api from '@/api'

export default {
  name: 'Register',
  components: {
    User,
    Lock,
    CircleCheck,
    Key,
    CirclePlus,
    StarFilled
  },
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
      
      if (form.value.password !== form.value.confirmPassword) {
        ElMessage.warning('两次输入的密码不一致')
        return
      }

      loading.value = true
      try {
        const { username, password } = form.value
        await api.post('/auth/register', { username, password })
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (err) {
        const msg = err.response?.data?.msg || err.message
        ElMessage.error(msg)
      } finally {
        loading.value = false
      }
    }

    return {
      form,
      loading,
      onSubmit
    }
  }
}
</script>

<style scoped>
.auth-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.auth-wrapper {
  width: 100%;
  max-width: 400px;
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 24px;
}

.logo-icon {
  font-size: 2.5rem;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.logo-text {
  font-size: 2rem;
  font-weight: 700;
  color: white;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.auth-title {
  font-size: 1.8rem;
  font-weight: 600;
  color: white;
  margin-bottom: 8px;
}

.auth-subtitle {
  color: rgba(255, 255, 255, 0.8);
  font-size: 1rem;
  margin: 0;
}

.auth-card {
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: none;
  padding: 32px;
  margin-bottom: 24px;
}

.auth-form {
  margin-bottom: 24px;
}

.auth-btn {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.auth-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.auth-footer {
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.auth-footer p {
  color: var(--text-secondary);
  margin: 0;
}

.link-btn {
  color: var(--primary-color);
  font-weight: 600;
  padding: 4px 8px;
}

.link-btn:hover {
  color: var(--info-color);
}

.auth-features {
  display: flex;
  justify-content: space-around;
  gap: 16px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.9rem;
}

.feature-icon {
  font-size: 1.5rem;
  color: rgba(255, 255, 255, 0.8);
}

/* Responsive Design */
@media (max-width: 480px) {
  .auth-card {
    padding: 24px;
  }
  
  .auth-title {
    font-size: 1.5rem;
  }
  
  .auth-features {
    flex-direction: column;
    gap: 12px;
  }
}
</style>