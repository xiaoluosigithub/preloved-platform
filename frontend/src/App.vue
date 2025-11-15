<template>
  <div>
    <div style="padding:8px 16px;border-bottom:1px solid #eee;display:flex;justify-content:space-between;align-items:center">
      <div>Secondhand</div>
      <div>
        <span v-if="user && user.username">已登录：{{ user.nickname || user.username }}</span>
        <span v-else>未登录</span>
        <el-button type="text" v-if="user && user.username" @click="logout">退出</el-button>
        <el-button type="text" v-else @click="$router.push('/login')">登录</el-button>
      </div>
    </div>
    <router-view />
  </div>
</template>

<script>
export default {
  data(){ return { user: null } },
  mounted(){
    this.refreshUser()
    window.addEventListener('storage', this.onStorage)
  },
  beforeUnmount(){
    window.removeEventListener('storage', this.onStorage)
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
