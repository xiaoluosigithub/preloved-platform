<template>
  <div style="max-width:1000px;margin:20px auto;display:flex;gap:24px">
    <div style="width:220px">
      <el-menu :default-active="active">
        <el-menu-item index="home" @click="$router.push('/')">返回首页</el-menu-item>
        <el-menu-item index="info" @click="go('/profile')">我的资料</el-menu-item>
        <el-menu-item index="my" @click="go('/profile/my-products')">我的发布</el-menu-item>
        <el-menu-item index="fav" @click="go('/profile/favorite')">我的收藏</el-menu-item>
        <el-menu-item index="like" @click="go('/profile/like')">我的点赞</el-menu-item>
        <el-menu-item index="logout" @click="logout">退出登录</el-menu-item>
      </el-menu>
      <div style="margin-top:16px">
        <el-card>
          <div style="display:flex;align-items:center;gap:12px">
            <img v-if="user.avatar" :src="user.avatar" style="width:48px;height:48px;border-radius:50%"/>
            <div>
              <div style="font-weight:600">{{ user.nickname || user.username }}</div>
              <div style="color:#999">{{ user.signature }}</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <div style="flex:1">
      <router-view />
    </div>
  </div>
</template>

<script>
import api from '@/api'
export default {
  data(){ return { active:'info', user:{} } },
  async mounted(){
    await this.load()
    this.setActive()
  },
  watch: { '$route.path'(){ this.setActive() } },
  methods:{
    async load(){
      const res = await api.get('/user/me')
      this.user = res.data.data || {}
    },
    setActive(){
      const p = this.$route.path
      if (p.endsWith('/my-products')) this.active='my'
      else if (p.endsWith('/favorite')) this.active='fav'
      else if (p.endsWith('/like')) this.active='like'
      else this.active='info'
    },
    go(p){ this.$router.push(p) },
    logout(){ localStorage.removeItem('token'); localStorage.removeItem('user'); this.$router.push('/login') }
  }
}
</script>