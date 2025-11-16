<template>
  <div style="max-width:1000px;margin:20px auto">
    <h3>我的点赞</h3>
    <el-row :gutter="20">
      <el-col v-for="p in items" :key="p.productId" :span="6">
        <product-card :product="toProduct(p)">
          <template #meta="{ product }">
            <div class="product-meta-inside">
              <div>商品名称：{{ product.title }}</div>
              <div>商品价格：¥{{ product.price }}</div>
            </div>
          </template>
        </product-card>
        <div class="action-center">
          <el-button size="small" @click.stop="toggleLike(p)">取消点赞</el-button>
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
      const res = await api.get('/product-interact/my-like', { params:{ page:this.page, size:this.size } })
      const d = res.data.data || {}
      this.items = d.items || []
      this.total = d.total || 0
    },
    onPageChange(p){ this.page=p; this.fetch() },
    toProduct(p){ return { id: p.productId, title: p.title, price: p.price, images: p.images, status: p.status } },
    async toggleLike(p){
      try { const res = await api.post(`/products/${p.productId}/like`); const d = res.data.data || {}; if (!d.liked) { this.items = this.items.filter(x=>x.productId!==p.productId); this.$message.success('已取消点赞') } }
      catch(e){ this.$message.error(e.response?.data?.msg || '操作失败') }
    }
  }
}
</script>

<style scoped>
.product-meta-inside { color: var(--text-primary) }
.action-center { text-align: center; margin-top: 6px }
:deep(.el-card__body) { padding: 10px }
</style>