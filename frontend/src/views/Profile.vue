<template>
  <div class="profile-container">
    <div class="profile-layout">
      <ProfileSidebar 
        :user="user"
        :active="active"
        @navigate="go"
        @logout="logout"
      />

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
import { User } from '@element-plus/icons-vue'
import api from '@/api'
import ProfileSidebar from '@/components/profile/ProfileSidebar.vue'

export default {
  name: 'Profile',
  components: {
    User,
    ProfileSidebar
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const active = ref('info')
    const user = ref({})

    const load = async () => {
      try {
        const res = await api.get('/user/me')
        user.value = res.data.data || {}
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