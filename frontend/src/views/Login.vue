<template>
  <div style="max-width:400px;margin:40px auto">
    <el-card>
      <h3>登录</h3>
      <el-form :model="form" @submit.prevent="onSubmit">
        <el-form-item label="用户名">
          <el-input v-model="form.username"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="form.password"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">登录</el-button>
          <el-button @click="$router.push('/register')">去注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import api from '@/api';
export default {
  data(){ return { form:{ username:'', password:'' } } },
  methods:{
    async onSubmit(){
      try {
        const res = await api.post('/auth/login', this.form);
        const { code, msg, data } = res.data || {};
        if (code !== 0 || !data || !data.token) {
          throw new Error(msg || '登录失败');
        }
        const token = data.token;
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(data.user || {}));
        window.dispatchEvent(new Event('auth-changed'));
        this.$router.push('/');
      } catch (err) {
        const msg = err.response?.data?.msg || err.message;
        this.$message.error(msg);
      }
    }
  }
}
</script>