<template>
  <div style="max-width:900px;margin:20px auto">
    <el-card>
      <h2>{{ product.title }}</h2>
      <div>¥ {{ product.price }}</div>
      <div style="margin:12px 0">
        <img v-for="(u,i) in images" :key="i" :src="u" style="width:160px;margin-right:8px"/>
      </div>
      <div style="display:flex;gap:8px">
        <el-button type="primary" @click="confirmBuy">确认购买</el-button>
        <el-button @click="$router.push('/product/'+product.id)">取消</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import api from '@/api'
import { ElMessageBox } from 'element-plus'
export default {
  data(){ return { product:{}, images:[], loading:false } },
  async mounted(){
    const pid = this.$route.params.productId
    const res = await api.get(`/products/${pid}`)
    const d = res.data.data || {}
    this.product = d.product || d || {}
    try { const arr = Array.isArray(this.product.images)? this.product.images : JSON.parse(this.product.images || '[]'); this.images = arr.map(x=> typeof x==='string'? x : (x?.url || x?.src || '')) } catch(e){ this.images=[] }
  },
  methods:{
    async confirmBuy(){
      if (this.loading) return
      const u = localStorage.getItem('user')
      const me = u ? JSON.parse(u) : null
      const buyerId = Number(me?.id)
      const sellerId = Number(this.product?.sellerId)
      if (!Number.isNaN(buyerId) && !Number.isNaN(sellerId) && buyerId === sellerId) { ElMessageBox.alert('不能购买自己发布的商品', '提示'); return }
      this.loading = true
      try { await api.post('/order/create', null, { params:{ productId: this.product.id } }); this.$message.success('下单成功'); this.$router.push('/order/list/buy') }
      catch(e){ this.$message.error(e.response?.data?.msg || '下单失败') }
      finally { this.loading = false }
    }
  }
}
</script>