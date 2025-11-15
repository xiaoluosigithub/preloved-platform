<template>
  <div style="max-width:400px;margin:40px auto">
    <el-card>
      <h3>注册</h3>
      <el-form :model="form" @submit.prevent="onSubmit">
        <el-form-item label="用户名">
          <el-input v-model="form.username"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="form.password"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">注册</el-button>
          <el-button @click="$router.push('/login')">去登录</el-button>
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
        await api.post('/auth/register', this.form);
        this.$message.success('注册成功，请登录');
        this.$router.push('/login');
      } catch (err) {
        const msg = err.response?.data?.msg || err.message;
        this.$message.error(msg);
      }
    }
  }
}
</script>