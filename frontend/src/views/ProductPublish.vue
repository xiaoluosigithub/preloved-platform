<template>
  <div style="max-width:800px;margin:20px auto">
    <el-form :model="form" label-width="100px">
      <el-form-item label="标题"><el-input v-model="form.title"/></el-form-item>
      <el-form-item label="价格"><el-input-number v-model="form.price" :min="0"/></el-form-item>
      <el-form-item label="分类">
        <el-select v-model="form.categoryId" placeholder="选择分类">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id"/>
        </el-select>
      </el-form-item>
      <el-form-item label="图片">
        <el-upload
          :action="uploadUrl"
          :on-success="handleUploadSuccess"
          :show-file-list="false"
          :headers="uploadHeaders"
          accept="image/*"
          name="file">
          <el-button>上传图片</el-button>
        </el-upload>
        <div style="margin-top:8px">
          <img v-for="(u,i) in form.images" :key="i" :src="u" style="width:100px;margin-right:8px"/>
        </div>
      </el-form-item>
      <el-form-item label="描述">
        <el-input type="textarea" v-model="form.description"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">发布</el-button>
        <el-button @click="$router.back()" style="margin-left:8px">取消发布</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import api from '@/api'
export default {
  data(){ return { form:{ title:'', price:0, categoryId:null, images:[], description:'' }, categories:[], uploadUrl:'/api/upload/image', uploadHeaders: { Authorization: `Bearer ${localStorage.getItem('token') || ''}` } } },
  mounted(){ this.fetchCategories() },
  methods:{
    async fetchCategories(){
      try {
        const res = await api.get('/categories')
        this.categories = res.data.data || []
      } catch(e){
        this.categories = [{id:1,name:'电子'},{id:2,name:'书籍'}]
      }
    },
    handleUploadSuccess(res){
      const url = res?.data?.data || res?.data || res
      if (url) this.form.images.push(url)
    },
    async submit(){
      try {
        await api.post('/products', { ...this.form, images: JSON.stringify(this.form.images) })
        this.$message.success('发布成功')
        this.$router.push('/')
      } catch (e) {
        this.$message.error(e.response?.data?.msg || e.message)
      }
    }
  }
}
</script>