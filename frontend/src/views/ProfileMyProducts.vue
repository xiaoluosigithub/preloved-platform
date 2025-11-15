<template>
  <div style="max-width:1000px;margin:20px auto">
    <h3>我的发布</h3>
    <el-row :gutter="20">
      <el-col v-for="p in items" :key="p.id" :span="6">
        <product-card :product="p" />
        <div style="margin-top:6px">
          <el-button size="small" @click="openEdit(p)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(p)">删除</el-button>
        </div>
      </el-col>
    </el-row>
    <div style="text-align:center;margin:20px 0">
      <el-pagination background layout="prev, pager, next" :page-size="size" :current-page="page" :total="total" @current-change="onPageChange"/>
    </div>

    <el-dialog v-model="show" title="编辑商品" width="600px">
      <el-form :model="edit">
        <el-form-item label="标题"><el-input v-model="edit.title"/></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="edit.price" :min="0"/></el-form-item>
        <el-form-item label="描述"><el-input type="textarea" v-model="edit.description"/></el-form-item>
        <el-form-item label="图片">
          <el-upload :action="'/api/upload/image'" :on-success="onImageUpload" :show-file-list="false" name="file">
            <el-button>上传图片</el-button>
          </el-upload>
          <div style="margin-top:8px">
            <img v-for="(u,i) in edit.images" :key="i" :src="u" style="width:80px;margin-right:8px"/>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="show=false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import api from '@/api'
import ProductCard from '@/components/ProductCard.vue'
export default {
  components:{ ProductCard },
  data(){ return { items:[], page:1, size:8, total:0, show:false, edit:{ images:[] } } },
  mounted(){ this.fetch() },
  methods:{
    async fetch(){
      const res = await api.get('/product/my', { params:{ page:this.page, size:this.size } })
      const d = res.data.data || {}
      this.items = d.items || []
      this.total = d.total || 0
    },
    onPageChange(p){ this.page=p; this.fetch() },
    openEdit(p){
      let imgs = []
      try { const arr = Array.isArray(p.images) ? p.images : JSON.parse(p.images || '[]'); imgs = arr.map(x => typeof x==='string'? x : (x?.url || x?.src || '')) } catch(e){}
      this.edit = { id:p.id, title:p.title, price:p.price, description:p.description, images: imgs }
      this.show=true
    },
    onImageUpload(res){ const url = res?.data?.data || res?.data || res; if (url) this.edit.images.push(url) },
    async saveEdit(){
      const payload = { ...this.edit, images: JSON.stringify(this.edit.images || []) }
      try { await api.post('/product/update', payload); this.$message.success('已保存'); this.show=false; this.fetch() } catch(e){ this.$message.error(e.response?.data?.msg || '保存失败') }
    },
    async del(p){
      try { await api.delete(`/product/${p.id}`); this.$message.success('已删除'); this.fetch() } catch(e){ this.$message.error(e.response?.data?.msg || '删除失败') }
    }
  }
}
</script>