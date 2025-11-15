<template>
  <div class="home-container">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">发现优质二手好物</h1>
        <p class="hero-subtitle">让闲置物品重新焕发生命力，环保又经济</p>
      </div>
    </section>

    <section class="search-section">
      <el-card class="search-card">
        <SearchBar 
          :categories="categories"
          :selected-category="selectedCategory"
          :keyword="keyword"
          @update:selectedCategory="val => selectedCategory = val"
          @update:keyword="val => keyword = val"
          @search="fetchList"
        />
        <div class="search-group">
          <el-button 
            type="success" 
            @click="$router.push('/publish')"
            class="publish-btn">
            <el-icon><Plus /></el-icon>
            发布商品
          </el-button>
        </div>
      </el-card>
    </section>

    <ProductsGrid 
      :items="items"
      :loading="loading"
      :total="total"
      :page="page"
      :size="size"
      @page-change="onPageChange"
      @size-change="onSizeChange"
    />
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import api from '@/api'
import SearchBar from '@/components/home/SearchBar.vue'
import ProductsGrid from '@/components/home/ProductsGrid.vue'

export default {
  name: 'Home',
  components: { 
    SearchBar,
    ProductsGrid,
    Plus
  },
  setup() {
    const categories = ref([])
    const selectedCategory = ref('')
    const keyword = ref('')
    const items = ref([])
    const page = ref(1)
    const size = ref(12)
    const total = ref(0)
    const loading = ref(false)

    const fetchCategories = async () => {
      try {
        const res = await api.get('/categories')
        categories.value = res.data.data || []
      } catch (e) {
        console.error('Failed to fetch categories:', e)
      }
    }

    const fetchList = async () => {
      loading.value = true
      try {
        const params = { 
          page: page.value, 
          size: size.value 
        }
        
        if (selectedCategory.value !== '' && selectedCategory.value != null) {
          params.categoryId = selectedCategory.value
        }
        
        const kw = (keyword.value || '').trim()
        if (kw) {
          params.keyword = kw
        }
        
        const res = await api.get('/products', { params })
        const d = res.data.data || {}
        items.value = d.items || []
        total.value = d.total || 0
      } catch (e) {
        console.error('Failed to fetch products:', e)
        items.value = []
        total.value = 0
      } finally {
        loading.value = false
      }
    }

    const onPageChange = (p) => {
      page.value = p
      fetchList()
    }

    const onSizeChange = (s) => {
      size.value = s
      page.value = 1
      fetchList()
    }

    onMounted(() => {
      fetchCategories()
      fetchList()
    })

    return {
      categories,
      selectedCategory,
      keyword,
      items,
      page,
      size,
      total,
      loading,
      fetchList,
      onPageChange,
      onSizeChange
    }
  }
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  padding: 0 0 40px 0;
  background: linear-gradient(135deg, #fff5e1 0%, #fde2e4 50%, #e1f0ff 100%);
}

/* Hero Section */
.hero-section {
  background: transparent;
  color: #111;
  padding: 60px 20px;
  text-align: center;
  margin-bottom: 40px;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero-title {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 16px;
  color: #111;
  text-shadow: none;
}

.hero-subtitle {
  font-size: 1.2rem;
  margin: 0;
  color: #333;
}

/* Search Section */
.search-section {
  max-width: 1200px;
  margin: 0 auto 40px;
  padding: 0 20px;
}

.search-card {
  border-radius: 16px;
  box-shadow: var(--shadow-lg);
  border: none;
}

.search-controls {
  display: flex;
  gap: 20px;
  align-items: center;
  padding: 20px;
}

.search-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input-group {
  flex: 1;
  display: flex;
  gap: 0;
}

.search-input {
  border-radius: 8px 0 0 8px;
  border-right: none;
}

.search-btn {
  border-radius: 0 8px 8px 0;
  padding: 12px 24px;
  font-weight: 600;
}

.category-select {
  min-width: 180px;
}

.publish-btn {
  padding: 12px 24px;
  font-weight: 600;
  border-radius: 8px;
}

/* Products Section */
.products-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 1.8rem;
  font-weight: 700;
  color: #111;
  margin: 0;
}

.results-info {
  color: #111;
  font-size: 0.9rem;
}

/* Products Grid */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.product-card-wrapper {
  transition: all var(--transition-base);
}

.product-card-wrapper:hover {
  transform: translateY(-4px);
}

.product-card {
  height: 100%;
  border-radius: 12px;
  overflow: hidden;
}

/* Loading and Empty States */
.loading-container {
  padding: 40px;
  text-align: center;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

/* Pagination */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.modern-pagination {
  padding: 10px;
  border-radius: 12px;
  background: white;
  box-shadow: var(--shadow-sm);
}

/* Responsive Design */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2rem;
  }
  
  .hero-subtitle {
    font-size: 1rem;
  }
  
  .search-controls {
    flex-direction: column;
    gap: 16px;
  }
  
  .search-input-group {
    width: 100%;
  }
  
  .category-select {
    width: 100%;
  }
  
  .section-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 16px;
  }
}

@media (max-width: 480px) {
  .products-grid {
    grid-template-columns: 1fr;
  }
  
  .search-card {
    margin: 0 10px;
  }
}
</style>