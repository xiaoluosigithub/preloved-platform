<template>
  <div>
    <div style="padding:8px 16px;border-bottom:1px solid #eee;display:flex;justify-content:space-between;align-items:center">
      <div style="cursor:pointer" @click="$router.push('/')">Secondhand</div>
      <div style="display:flex;align-items:center;gap:8px">
        <img v-if="user && user.username" :src="user.avatar || defaultAvatar" style="width:28px;height:28px;border-radius:50%" />
        <span v-if="user && user.username">{{ user.nickname || '未设置昵称' }}</span>
        <span v-else>未登录</span>
        <el-button type="text" v-if="user && user.username" @click="$router.push('/profile')">个人主页</el-button>
        <el-button type="text" v-if="user && user.username" @click="logout">退出</el-button>
        <el-button type="text" v-else @click="$router.push('/login')">登录</el-button>
      </div>
    </div>
    <router-view />
  </div>
</template>

<script>
export default {
  data(){ return { user: null, defaultAvatar: 'https://via.placeholder.com/28' } },
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
    },
    onStorage(e){
      if (e.key === 'user' || e.key === 'token') this.refreshUser()
    },
    logout(){
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      this.refreshUser()
      this.$router.push('/login')
    }
  }
}
</script>
