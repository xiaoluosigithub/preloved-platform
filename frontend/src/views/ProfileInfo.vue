<template>
  <el-card>
    <div>
      <h3>我的资料</h3>
      <el-form :model="form" label-width="90px">
        <el-form-item label="昵称"><el-input v-model="form.nickname"/></el-form-item>
        <el-form-item label="头像">
          <div style="display:flex;align-items:center;gap:12px">
            <img v-if="form.avatar" :src="form.avatar" style="width:60px;height:60px;border-radius:50%"/>
            <el-upload :action="'/api/upload/image'" :on-success="onAvatarUpload" :show-file-list="false" name="file">
              <el-button>上传头像</el-button>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="签名"><el-input v-model="form.signature"/></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save">保存</el-button>
        </el-form-item>
      </el-form>
    </div>
  </el-card>
</template>

<script>
import api from '@/api'
export default {
  data(){ return { form:{ nickname:'', avatar:'', signature:'' } } },
  async mounted(){
    const res = await api.get('/user/me')
    const u = res.data.data || {}
    this.form.nickname = u.nickname || ''
    this.form.avatar = u.avatar || ''
    this.form.signature = u.signature || ''
  },
  methods:{
    onAvatarUpload(res){ const url = res?.data?.data || res?.data || res; if (url) this.form.avatar = url },
    async save(){
      try { await api.post('/user/update', this.form); this.$message.success('保存成功') } catch(e){ this.$message.error(e.response?.data?.msg || '保存失败') }
    }
  }
}
</script>