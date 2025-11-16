<template>
  <div class="profile-container">
    <div class="profile-layout">
      <!-- Sidebar -->
      <aside class="profile-sidebar">
        <!-- User Info Card -->
        <el-card class="user-info-card">
          <div class="user-avatar-section">
            <img 
              v-if="user.avatar" 
              :src="user.avatar" 
              class="user-avatar"
              alt="用户头像"
            />
            <div v-else class="user-avatar-placeholder">
              <el-icon size="24"><User /></el-icon>
            </div>
            <div class="user-details">
              <h3 class="user-name">{{ user.nickname || user.username }}</h3>
              <p class="user-signature">{{ user.signature || '暂无个性签名' }}</p>
            </div>
          </div>
        </el-card>

        <!-- Navigation Menu -->
        <el-card class="nav-card">
          <div class="admin-actions">
            <el-button v-if="isAdmin" type="warning" class="nav-btn" @click="$router.push('/admin/login')">
              进入管理员界面
            </el-button>
            <el-button type="primary" class="nav-btn" @click="$router.push('/')">
              <el-icon class="nav-icon"><House /></el-icon>
              返回首页
            </el-button>
          </div>
          <el-menu :default-active="active" class="profile-menu" @select="handleMenuSelect">
            <el-menu-item index="info" @click="go('/profile')">
              <el-icon><User /></el-icon>
              <span>我的资料</span>
            </el-menu-item>
            <el-menu-item index="my" @click="go('/profile/my-products')">
              <el-icon><Goods /></el-icon>
              <span>我的发布</span>
            </el-menu-item>
            <el-menu-item index="fav" @click="go('/profile/favorite')">
              <el-icon><Star /></el-icon>
              <span>我的收藏</span>
            </el-menu-item>
            <el-menu-item index="like" @click="go('/profile/like')">
              <el-icon><View /></el-icon>
              <span>我的点赞</span>
            </el-menu-item>
            <el-menu-item index="order-buy" @click="go('/profile/order-buy')">
              <el-icon><ShoppingCart /></el-icon>
              <span>我买到的</span>
            </el-menu-item>
            <el-menu-item index="order-sell" @click="go('/profile/order-sell')">
              <el-icon><Sell /></el-icon>
              <span>我卖出的</span>
            </el-menu-item>
          </el-menu>
        </el-card>

        <!-- Logout Button -->
        <el-button 
          type="danger" 
          @click="logout" 
          class="logout-btn"
          plain>
          <el-icon><SwitchButton /></el-icon>
          退出登录
        </el-button>
      </aside>

      <!-- Main Content -->
      <main class="profile-main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  User, 
  House, 
  Goods, 
  Star, 
  ShoppingCart, 
  Sell, 
  View, 
  SwitchButton 
} from '@element-plus/icons-vue'
import api from '@/api'

export default {
  name: 'Profile',
  components: {
    User,
    House,
    Goods,
    Star,
    ShoppingCart,
    Sell,
    View,
    SwitchButton
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const active = ref('info')
    const user = ref({})
    const isAdmin = ref(false)

    const load = async () => {
      try {
        const res = await api.get('/user/me')
        user.value = res.data.data || {}
        isAdmin.value = String(user.value.role || '').toUpperCase() === 'ADMIN'
      } catch (err) {
        console.error('Failed to load user info:', err)
      }
    }

    const setActive = () => {
      const p = route.path
      if (p.endsWith('/my-products')) active.value = 'my'
      else if (p.endsWith('/favorite')) active.value = 'fav'
      else if (p.endsWith('/like')) active.value = 'like'
      else if (p.endsWith('/order-buy')) active.value = 'order-buy'
      else if (p.endsWith('/order-sell')) active.value = 'order-sell'
      else active.value = 'info'
    }

    const go = (path) => {
      router.push(path)
    }

    const handleMenuSelect = (index) => {
      // Menu selection is handled by individual menu item clicks
    }

    const logout = () => {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
    }

    onMounted(async () => {
      await load()
      setActive()
    })

    watch(() => route.path, () => {
      setActive()
    })

    return {
      active,
      user,
      isAdmin,
      go,
      handleMenuSelect,
      logout
    }
  }
}
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.profile-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.profile-sidebar {
  width: 280px;
  flex-shrink: 0;
}

.user-info-card {
  margin-bottom: 16px;
  border-radius: 12px;
  box-shadow: var(--shadow-sm);
  border: none;
}

.user-avatar-section {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
}

.user-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid var(--primary-light);
  box-shadow: var(--shadow-sm);
}

.user-avatar-placeholder {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: var(--primary-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.5rem;
  box-shadow: var(--shadow-sm);
}

.user-details {
  flex: 1;
}

.user-name {
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 4px 0;
}

.user-signature {
  color: var(--text-secondary);
  font-size: 0.9rem;
  margin: 0;
  line-height: 1.4;
}

  .nav-card {
    border-radius: 12px;
    box-shadow: var(--shadow-sm);
    border: none;
    margin-bottom: 16px;
  }

  .nav-card :deep(.el-card__body) { padding: 12px; }

  .admin-actions { display: flex; flex-direction: column; gap: 12px; }

  .nav-btn { display: block; width: 100%; height: 48px; border-radius: 8px; font-weight: 600; margin: 0; }
  .nav-icon { margin-right: 8px; }

  .profile-menu { margin-top: 8px; }
  .admin-actions :deep(.el-button + .el-button) { margin-left: 0 !important; }

.profile-menu {
  border: none;
  background: transparent;
}

.profile-menu .el-menu-item {
  border-radius: 8px;
  margin: 4px 8px;
  height: 48px;
  line-height: 48px;
  border: none;
  transition: all 0.3s ease;
  color: var(--text-primary);
  background: white;
}

.profile-menu .el-menu-item:hover {
  background: var(--primary-light);
  color: var(--primary-color);
}

.profile-menu .el-menu-item.is-active {
  background: #409EFF;
  color: white;
  font-weight: 600;
}

.home-btn { width: 100%; height: 48px; border-radius: 8px; font-weight: 600; margin-bottom: 8px }

.logout-btn {
  width: 100%;
  border-radius: 8px;
  height: 48px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.profile-main {
  flex: 1;
  min-height: 600px;
}

/* Responsive Design */
@media (max-width: 768px) {
  .profile-layout {
    flex-direction: column;
  }
  
  .profile-sidebar {
    width: 100%;
  }
  
  .user-avatar-section {
    justify-content: center;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .profile-container {
    padding: 0 12px;
    margin: 12px auto;
  }
  
  .profile-layout {
    gap: 16px;
  }
}
</style>