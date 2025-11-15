<template>
  <div style="max-width:1000px;margin:20px auto">
    <h3>我的收藏</h3>
    <el-row :gutter="20">
      <el-col v-for="p in items" :key="p.productId" :span="6">
        <product-card :product="toProduct(p)" />
        <div style="margin-top:6px">
          <el-button size="small" @click.stop="toggleFavorite(p)">取消收藏</el-button>
        </div>
      </el-col>
    </el-row>
    <div style="text-align:center;margin:20px 0">
      <el-pagination background layout="prev, pager, next" :page-size="size" :current-page="page" :total="total" @current-change="onPageChange"/>
    </div>
  </div>
</template>

<script>
import api from '@/api'
import ProductCard from '@/components/ProductCard.vue'
export default {
  components:{ ProductCard },
  data(){ return { items:[], page:1, size:8, total:0 } },
  mounted(){ this.fetch() },
  methods:{
    async fetch(){
      const res = await api.get('/product-interact/my-favorite', { params:{ page:this.page, size:this.size } })
      const d = res.data.data || {}
      this.items = d.items || []
      this.total = d.total || 0
    },
    onPageChange(p){ this.page=p; this.fetch() },
    toProduct(p){ return { id: p.productId, title: p.title, price: p.price, images: p.images, status: p.status } },
    async toggleFavorite(p){
      try { const res = await api.post(`/products/${p.productId}/favorite`); const d = res.data.data || {}; if (!d.favorited) { this.items = this.items.filter(x=>x.productId!==p.productId); this.$message.success('已取消收藏') } }
      catch(e){ this.$message.error(e.response?.data?.msg || '操作失败') }
    }
  }
}
</script>