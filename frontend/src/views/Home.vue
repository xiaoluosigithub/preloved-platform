<template>
  <div>
    <el-row style="margin:16px 0">
      <el-col :span="6">
        <el-select v-model="selectedCategory" placeholder="全部分类" @change="fetchList">
          <el-option :label="'全部'" :value="''"></el-option>
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-col>
      <el-col :span="12">
        <el-input v-model="keyword" placeholder="搜索商品" @keyup.enter="fetchList">
          <template #append>
            <el-button @click="fetchList">搜索</el-button>
          </template>
        </el-input>
      </el-col>
      <el-col :span="6" style="text-align:right">
        <el-button type="primary" @click="$router.push('/publish')">发布商品</el-button>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col v-for="p in items" :key="p.id" :span="6">
        <product-card :product="p" />
      </el-col>
    </el-row>

    <div style="text-align:center;margin:20px 0">
      <el-pagination
        background
        layout="prev, pager, next"
        :page-size="size"
        :current-page="page"
        :total="total"
        @current-change="onPageChange" />
    </div>
  </div>
</template>

<script>
import api from '@/api'
import ProductCard from '@/components/ProductCard.vue'
export default {
  components: { ProductCard },
  data(){ return { categories:[], selectedCategory:'', keyword:'', items:[], page:1, size:8, total:0 } },
  mounted(){ this.fetchCategories(); this.fetchList(); },
  methods:{
    async fetchCategories(){
      try {
        const res = await api.get('/categories')
        this.categories = res.data.data || []
      } catch (e) {
      }
    },
    async fetchList(){
      try {
        const params = { page:this.page, size:this.size, keyword:this.keyword }
        if (this.selectedCategory !== '' && this.selectedCategory != null) params.categoryId = this.selectedCategory
        const res = await api.get('/products', { params })
        const d = res.data.data || {}
        this.items = d.items || []
        this.total = d.total || 0
      } catch (e) { }
    },
    onPageChange(p){ this.page = p; this.fetchList(); }
  }
}
</script>