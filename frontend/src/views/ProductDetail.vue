<template>
  <div style="max-width:900px;margin:20px auto">
    <el-card>
      <h2>{{ product.title }}</h2>
      <div>¥ {{ product.price }}</div>
      <div style="margin:12px 0">
        <img v-for="(u,i) in images" :key="i" :src="u" style="width:160px;margin-right:8px"/>
      </div>
      <div v-html="product.description"></div>
    </el-card>
  </div>
</template>

<script>
import api from '@/api'
export default {
  props: ['id'],
  data(){ return { product:{}, images:[] } },
  async mounted(){
    const pid = this.$route.params.id || this.id
    const res = await api.get(`/products/${pid}`)
    this.product = res.data.data || {}
    try {
      const imgs = this.product.images
      const arr = Array.isArray(imgs) ? imgs : JSON.parse(imgs || '[]')
      this.images = arr.map(x => (typeof x === 'string') ? x : (x?.url || x?.src || x?.data || '')).filter(Boolean)
    } catch(e){
      this.images = (typeof this.product.images === 'string' && this.product.images.startsWith('/')) ? [this.product.images] : []
    }
  }
}
</script>