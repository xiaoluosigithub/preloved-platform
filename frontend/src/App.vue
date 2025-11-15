<template>
  <div class="app-container">
    <!-- Modern Header -->
    <header class="app-header">
      <div class="header-content">
        <!-- Logo -->
        <div class="logo-section" @click="$router.push('/')">
          <div class="logo-icon">🛍️</div>
          <h1 class="logo-text">Secondhand</h1>
        </div>

        <!-- Navigation -->
        <nav class="main-nav"></nav>

        <!-- User Section -->
        <div class="user-section">
          <div v-if="user && user.username" class="user-info">
            <el-dropdown trigger="click" @command="handleCommand">
              <div class="user-avatar-wrapper">
                <img 
                  :src="(user && user.avatar && user.avatar.trim()) ? user.avatar : defaultAvatar" 
                  class="user-avatar"
                  :alt="user.nickname || user.username"
                  @error="onAvatarError" />
                <span class="username">{{ user.nickname || user.username }}</span>
                <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人主页
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <div v-else class="guest-section">
            <el-button 
              type="primary" 
              @click="$router.push('/login')"
              class="login-btn">
              登录
            </el-button>
            <el-button 
              @click="$router.push('/register')"
              class="register-btn">
              注册
            </el-button>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- Footer -->
    <footer class="app-footer">
      <div class="footer-content">
        <p>&copy; 2024 Secondhand. 让闲置物品重新焕发生命力.</p>
      </div>
    </footer>
  </div>
</template>

<script>
import { ArrowDown, Plus, User, SwitchButton } from '@element-plus/icons-vue'
import api from '@/api'

export default {
  name: 'App',
  components: {
    ArrowDown,
    Plus,
    User,
    SwitchButton
  },
  data() { 
    return { 
      user: null, 
      defaultAvatar: '/default-avatar.svg' 
    } 
  },
  mounted(){
    this.refreshUser()
    window.addEventListener('storage', this.onStorage)
    window.addEventListener('auth-changed', this.refreshUser)
  },
  beforeUnmount(){
    window.removeEventListener('storage', this.onStorage)
    window.removeEventListener('auth-changed', this.refreshUser)
  },
  methods:{
    refreshUser(){
      const u = localStorage.getItem('user')
      this.user = u ? JSON.parse(u) : null
      const token = localStorage.getItem('token')
      if (token) {
        api.get('/user/me').then(res => {
          const data = res?.data?.data || {}
          this.user = data
          try { localStorage.setItem('user', JSON.stringify(data)) } catch(e) {}
        }).catch(() => {})
      }
    },
    onAvatarError(e){
      e.target.src = this.defaultAvatar
    },
    onStorage(e){
      if (e.key === 'user' || e.key === 'token') this.refreshUser()
    },
    handleCommand(command) {
      if (command === 'profile') {
        this.$router.push('/profile')
      } else if (command === 'logout') {
        this.logout()
      }
    },
    logout(){
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      this.refreshUser()
      this.$message.success('已退出登录')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.app-header {
  background: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(10px);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.logo-section:hover {
  transform: scale(1.02);
}

.logo-icon {
  font-size: 2rem;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.logo-text {
  font-size: 1.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0;
}

.main-nav {
  flex: 1;
  display: flex;
  justify-content: center;
}

.publish-btn {
  border-radius: 25px;
  padding: 10px 20px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.publish-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.user-section {
  min-width: 200px;
  display: flex;
  justify-content: flex-end;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 25px;
  background: #f8f9fa;
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-avatar-wrapper:hover {
  background: #e9ecef;
  transform: translateY(-1px);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.username {
  font-weight: 600;
  color: #2c3e50;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dropdown-icon {
  color: #95a5a6;
  font-size: 12px;
}

.guest-section {
  display: flex;
  gap: 12px;
}

.login-btn, .register-btn {
  border-radius: 20px;
  padding: 8px 16px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.login-btn:hover, .register-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.main-content {
  flex: 1;
  padding: 20px 0;
}

.app-footer {
  background: #2c3e50;
  color: white;
  padding: 20px 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  text-align: center;
}

.footer-content p {
  margin: 0;
  opacity: 0.8;
}

/* Responsive Design */
@media (max-width: 768px) {
  .header-content {
    padding: 0 15px;
    height: 60px;
  }
  
  .logo-text {
    font-size: 1.2rem;
  }
  
  .main-nav {
    display: none;
  }
  
  .user-section {
    min-width: auto;
  }
  
  .username {
    display: none;
  }
  
  .guest-section {
    gap: 8px;
  }
  
  .login-btn, .register-btn {
    padding: 6px 12px;
    font-size: 0.9rem;
  }
}
</style>
