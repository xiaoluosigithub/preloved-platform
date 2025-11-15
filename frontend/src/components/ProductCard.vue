<template>
  <el-card @click="goDetail" style="cursor:pointer">
    <div style="width:100%;height:160px;display:flex;align-items:center;justify-content:center;background:#f6f7f9">
      <img v-if="mainImage" :src="mainImage" style="width:100%;height:160px;object-fit:cover" />
      <div v-else style="color:#999">暂无图片</div>
    </div>
    <div style="padding:8px 0">
      <h4>{{ product.title }}</h4>
      <div>¥ {{ product.price }}</div>
    </div>
  </el-card>
</template>
<script>
export default {
  props: ['product'],
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