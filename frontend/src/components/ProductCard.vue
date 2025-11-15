<template>
  <div class="pc-card" @click="goDetail" style="cursor:pointer; position:relative">
    <div class="pc-image">
      <img v-if="mainImage" :src="mainImage" class="pc-img" />
      <div v-else class="pc-img-empty">暂无图片</div>
    </div>
    <div class="pc-info">
      <template v-if="showLabels">
        <div class="pc-row"><span class="pc-label">商品名称：</span><span class="pc-value">{{ product.title }}</span></div>
        <div class="pc-row"><span class="pc-label">商品价格：</span><span class="pc-value">¥ {{ product.price }}</span></div>
      </template>
      <template v-else>
        <h4 class="pc-title">{{ product.title }}</h4>
        <div class="pc-price">¥ {{ product.price }}</div>
      </template>
    </div>
    <div v-if="product.status==='SOLD'" class="pc-sold">已售出</div>
  </div>
</template>
<script>
export default {
  props: {
    product: { type: Object, required: true },
    showLabels: { type: Boolean, default: false }
  },
  computed: {
    mainImage(){
      try {
        const imgs = this.product.images
        const arr = Array.isArray(imgs) ? imgs : JSON.parse(imgs || '[]')
        if (arr.length > 0) {
          const first = arr[0]
          if (typeof first === 'string') return first
          if (first && typeof first === 'object') return first.url || first.src || first.data || ''
        }
      } catch(e){}
      if (typeof this.product.images === 'string' && this.product.images.startsWith('/')) return this.product.images
      return ''
    }
  },
  methods: {
    goDetail(){ this.$router.push(`/product/${this.product.id}`) }
  }
}
</script>
<style scoped>
.pc-card { padding: 0; }
.pc-image { width:100%; height:160px; display:flex; align-items:center; justify-content:center; background:#f6f7f9 }
.pc-img { width:100%; height:160px; object-fit:cover }
.pc-img-empty { color:#999 }
.pc-info { padding:0; margin-top:8px }
.pc-row { display:flex; gap:6px; line-height:1.6; margin-bottom:8px }
.pc-label { color:#666 }
.pc-value { color:#333 }
.pc-title { margin:0; font-size:16px }
.pc-price { color:#333 }
.pc-sold { position:absolute; top:8px; left:8px; background:#f56c6c; color:#fff; padding:2px 6px; border-radius:4px; font-size:12px }
</style>