<template>
  <div class="my-products-container">
    <!-- Header Section -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">我的发布</h1>
        <p class="page-subtitle">管理您发布的所有商品</p>
      </div>
      <div class="header-actions">
        <el-button 
          type="primary" 
          @click="$router.push('/publish')"
          class="publish-btn">
          <el-icon><Plus /></el-icon>
          发布新商品
        </el-button>
      </div>
    </div>

    <MyStatsCards :stats="stats" />

    <MyProductsFilter 
      :search-query="searchQuery"
      :status-filter="statusFilter"
      :sort-by="sortBy"
      @update:searchQuery="val => { searchQuery = val }"
      @update:statusFilter="val => { statusFilter = val }"
      @update:sortBy="val => { sortBy = val }"
      @search="handleSearch"
      @filter="handleFilter"
      @sort="handleSort"
      @reset="resetFilters"
    />

    <!-- Products Grid -->
    <div class="products-section">
      <div v-if="items.length === 0" class="empty-state">
        <el-icon size="64"><Goods /></el-icon>
        <h3>暂无发布的商品</h3>
        <p>您还没有发布任何商品，快去发布您的第一个商品吧！</p>
        <el-button type="primary" @click="$router.push('/publish')">
          立即发布
        </el-button>
      </div>
      
      <el-row v-else :gutter="20" class="products-grid">
        <el-col v-for="product in items" :key="product.id" :span="6" :xs="24" :sm="12" :md="8" :lg="6">
          <MyProductCard 
            :product="product"
            @edit="openEdit"
            @delete="del"
            @toggle-status="toggleStatus"
          />
        </el-col>
      </el-row>
    </div>

    <!-- Pagination -->
    <div v-if="total > 0" class="pagination-container">
      <el-pagination 
        background 
        layout="total, prev, pager, next, jumper" 
        :page-size="size" 
        :current-page="page" 
        :total="total" 
        @current-change="onPageChange"
        class="pagination"/>
    </div>

    <MyProductEditDialog 
      v-model:visible="show"
      v-model:modelValue="edit"
      :loading="loading"
      @save="saveEdit"
    />
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { 
  Plus, 
  Edit, 
  Delete, 
  Hide,
  View as ViewIcon,
  Goods
} from '@element-plus/icons-vue'
import api from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import MyStatsCards from '@/components/profile/MyStatsCards.vue'
import MyProductsFilter from '@/components/profile/MyProductsFilter.vue'
import MyProductCard from '@/components/profile/MyProductCard.vue'
import MyProductEditDialog from '@/components/profile/MyProductEditDialog.vue'

export default {
  name: 'ProfileMyProducts',
  components: {
    Plus,
    Edit,
    Delete,
    Hide,
    ViewIcon,
    Goods,
    MyStatsCards,
    MyProductsFilter,
    MyProductCard,
    MyProductEditDialog
  },
  setup() {
    const items = ref([])
    const page = ref(1)
    const size = ref(8)
    const total = ref(0)
    const show = ref(false)
    const loading = ref(false)
    const searchQuery = ref('')
    const statusFilter = ref('')
    const sortBy = ref('newest')
    
    const edit = ref({
      id: '',
      title: '',
      price: 0,
      description: '',
      images: []
    })

    const stats = computed(() => {
      const allItems = items.value
      return {
        active: allItems.filter(item => item.status === 'ACTIVE').length,
        sold: allItems.filter(item => item.status === 'SOLD').length,
        total: allItems.length,
        views: allItems.reduce((sum, item) => sum + (item.views || 0), 0)
      }
    })

    const fetch = async () => {
      try {
        const params = { 
          page: page.value, 
          size: size.value,
          search: searchQuery.value,
          status: statusFilter.value,
          sort: sortBy.value
        }
        const res = await api.get('/product/my', { params })
        const d = res.data.data || {}
        items.value = d.items || []
        total.value = d.total || 0
      } catch (err) {
        console.error('Failed to fetch products:', err)
        ElMessage.error('获取商品列表失败')
      }
    }

    const onPageChange = (p) => {
      page.value = p
      fetch()
    }

    

    const openEdit = (product) => {
      let imgs = []
      try { 
        const arr = Array.isArray(product.images) ? product.images : JSON.parse(product.images || '[]')
        imgs = arr.map(x => typeof x === 'string' ? x : (x?.url || x?.src || ''))
      } catch(e) {}
      
      edit.value = {
        id: product.id,
        title: product.title,
        price: product.price,
        description: product.description,
        images: imgs
      }
      show.value = true
    }

    

    const saveEdit = async () => {
      if (!edit.value.title?.trim()) {
        ElMessage.warning('请输入商品标题')
        return
      }
      if (edit.value.price <= 0) {
        ElMessage.warning('请输入有效的价格')
        return
      }

      loading.value = true
      try {
        const payload = { 
          ...edit.value, 
          images: JSON.stringify(edit.value.images || []) 
        }
        await api.post('/product/update', payload)
        ElMessage.success('商品信息已保存')
        show.value = false
        fetch()
      } catch (err) {
        const msg = err.response?.data?.msg || '保存失败'
        ElMessage.error(msg)
      } finally {
        loading.value = false
      }
    }

    const toggleStatus = async (product) => {
      try {
        const newStatus = product.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
        await api.post('/product/update', {
          id: product.id,
          status: newStatus
        })
        ElMessage.success(`商品已${newStatus === 'ACTIVE' ? '上架' : '下架'}`)
        fetch()
      } catch (err) {
        ElMessage.error('操作失败')
      }
    }

    const del = async (product) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除商品 "${product.title}" 吗？`,
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        await api.delete(`/product/${product.id}`)
        ElMessage.success('商品已删除')
        fetch()
      } catch (err) {
        if (err !== 'cancel') {
          const msg = err.response?.data?.msg || '删除失败'
          ElMessage.error(msg)
        }
      }
    }

    const handleSearch = () => {
      page.value = 1
      fetch()
    }

    const handleFilter = () => {
      page.value = 1
      fetch()
    }

    const handleSort = () => {
      page.value = 1
      fetch()
    }

    const resetFilters = () => {
      searchQuery.value = ''
      statusFilter.value = ''
      sortBy.value = 'newest'
      page.value = 1
      fetch()
    }

    onMounted(() => {
      fetch()
    })

    return {
      items,
      page,
      size,
      total,
      show,
      loading,
      searchQuery,
      statusFilter,
      sortBy,
      edit,
      stats,
      fetch,
      onPageChange,
      openEdit,
      saveEdit,
      toggleStatus,
      del,
      handleSearch,
      handleFilter,
      handleSort,
      resetFilters
    }
  }
}
</script>

<style scoped>
.my-products-container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 0 8px;
}

.header-content {
  flex: 1;
}

.page-title {
  font-size: 2rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.page-subtitle {
  color: var(--text-secondary);
  font-size: 1rem;
  margin: 0;
}

.publish-btn {
  background: #409EFF;
  border: none;
  color: #fff;
  font-weight: 600;
}

.publish-btn:hover {
  transform: none;
  box-shadow: none;
}

.stats-section {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 12px;
  box-shadow: var(--shadow-sm);
  border: none;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: none;
  box-shadow: var(--shadow-sm);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  color: white;
}

.stat-icon.active {
  background: var(--success-gradient);
}

.stat-icon.sold {
  background: var(--warning-gradient);
}

.stat-icon.total {
  background: var(--info-gradient);
}

.stat-icon.views {
  background: var(--primary-gradient);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.stat-label {
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.filter-card {
  border-radius: 12px;
  box-shadow: var(--shadow-sm);
  border: none;
  margin-bottom: 24px;
}

.products-section {
  margin-bottom: 24px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: var(--text-secondary);
}

.empty-state .el-icon {
  color: var(--border-color);
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 1.5rem;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.empty-state p {
  margin: 0 0 24px 0;
  font-size: 1rem;
}

.products-grid {
  margin: 0;
}

.product-card {
  border-radius: 12px;
  box-shadow: var(--shadow-sm);
  border: none;
  transition: all 0.3s ease;
  overflow: hidden;
  margin-bottom: 20px;
}

.product-card:hover {
  transform: none;
  box-shadow: var(--shadow-sm);
}

.product-card.sold-card {
  opacity: 0.7;
}

.product-image-container {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image {
  transform: none;
}

.sold-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
}

.sold-text {
  color: white;
  font-size: 1.2rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 2px;
}

.product-status {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
  color: white;
}

.product-status.active {
  background: var(--success-color);
}

.product-status.sold {
  background: var(--danger-color);
}

.product-status.inactive {
  background: var(--info-color);
}

.product-info {
  padding: 16px;
}

.product-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 1.3rem;
  font-weight: 700;
  color: var(--danger-color);
  margin: 0 0 4px 0;
}

.product-views {
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin: 0;
}

.product-actions {
  padding: 0 16px 16px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.product-actions .el-button {
  flex: 1;
  min-width: 60px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 24px 0;
}

.pagination {
  display: flex;
  justify-content: center;
}

.edit-dialog {
  border-radius: 16px;
}

.edit-form {
  padding: 8px 0;
}

.image-upload-section {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.upload-tip {
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.image-preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 12px;
}

.image-preview-item {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid var(--border-color);
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.image-preview-item:hover .image-overlay {
  opacity: 1;
}

/* Responsive Design */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .publish-btn {
    width: 100%;
  }
  
  .stat-content {
    padding: 16px;
  }
  
  .stat-number {
    font-size: 1.5rem;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 1.5rem;
  }
  
  .product-actions {
    flex-direction: column;
  }
  
  .product-actions .el-button {
    width: 100%;
  }
  
  .image-upload-section {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>