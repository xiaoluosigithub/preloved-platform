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

    <!-- Stats Cards -->
    <div class="stats-section">
      <el-row :gutter="16">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon active">
                <el-icon><Goods /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.available }}</div>
                <div class="stat-label">在售商品</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon sold">
                <el-icon><Sell /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.sold }}</div>
                <div class="stat-label">已售商品</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon views">
                <el-icon><View /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">-</div>
                <div class="stat-label">总浏览量</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- Filter Section -->
    <el-card class="filter-card">
      <el-row :gutter="16" align="middle">
        <el-col :span="8">
          <el-input
            v-model="searchQuery"
            placeholder="搜索商品名称..."
            clearable
            :prefix-icon="Search"
            @input="handleSearch">
          </el-input>
        </el-col>
        <el-col :span="6">
          <el-select v-model="statusFilter" placeholder="状态筛选" clearable @change="handleFilter">
            <el-option label="全部" value="" />
            <el-option label="未售出" value="AVAILABLE" />
            <el-option label="已售出" value="SOLD" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="sortBy" placeholder="排序方式" @change="handleSort">
            <el-option label="最新发布" value="newest" />
            <el-option label="价格从低到高" value="price_asc" />
            <el-option label="价格从高到低" value="price_desc" />
            <el-option label="浏览量最多" value="views" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button @click="resetFilters" :icon="Refresh">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- Products Grid -->
    <div class="products-section">
      <div v-if="displayItems.length === 0" class="empty-state">
        <el-icon size="64"><Goods /></el-icon>
        <h3>暂无发布的商品</h3>
        <p>您还没有发布任何商品，快去发布您的第一个商品吧！</p>
        <el-button type="primary" @click="$router.push('/publish')">
          立即发布
        </el-button>
      </div>
      
      <el-row v-else :gutter="20" class="products-grid">
        <el-col v-for="product in displayItems" :key="product.id" :span="6" :xs="24" :sm="12" :md="8" :lg="6">
          <el-card class="product-card" :class="{ 'sold-card': product.status === 'SOLD' }" @click="goDetail(product.id)" style="cursor:pointer">
            <div class="product-image-container">
              <template v-if="getMainImage(product)">
                <img 
                  :src="getMainImage(product)" 
                  class="product-image"
                  :alt="product.title"
                />
              </template>
              <template v-else>
                <div class="no-image-placeholder">暂无图片</div>
              </template>
              <div v-if="product.status === 'SOLD'" class="sold-overlay">
                <span class="sold-text">已售出</span>
              </div>
              <div v-if="product.status === 'SOLD'" class="product-status sold">
                已售出
              </div>
            </div>
            
            <div class="product-info">
              <div class="text-line">商品名称：{{ product.title }}</div>
              <div class="text-line">商品价格：¥{{ product.price }}</div>
              <p class="product-views text-line">浏览量: -</p>
            </div>
            
            <div class="product-actions">
              <el-button 
                size="small" 
                @click.stop="openEdit(product)" 
                :disabled="product.status === 'SOLD'"
                :icon="Edit" class="action-btn">
                编辑
              </el-button>
              <el-button 
                size="small" 
                type="danger" 
                @click.stop="del(product)" 
                :disabled="product.status === 'SOLD'"
                :icon="Delete" class="action-btn">
                删除
              </el-button>

            </div>
          </el-card>
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

    <!-- Edit Dialog -->
    <el-dialog 
      v-model="show" 
      title="编辑商品" 
      width="700px"
      class="edit-dialog">
      <el-form :model="edit" label-position="top" class="edit-form">
        <el-row :gutter="16">
          <el-col :span="16">
            <el-form-item label="商品标题">
              <el-input 
                v-model="edit.title" 
                placeholder="请输入商品标题"
                size="large"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="价格 (元)">
              <el-input-number 
                v-model="edit.price" 
                :min="0" 
                :precision="2"
                size="large"
                style="width: 100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="商品描述">
          <el-input 
            type="textarea" 
            v-model="edit.description"
            :rows="4"
            placeholder="请详细描述您的商品..."
            maxlength="500"
            show-word-limit/>
        </el-form-item>
        
        <el-form-item label="商品图片">
          <div class="image-upload-section">
            <el-upload 
              :action="'/api/upload/image'" 
              :on-success="onImageUpload" 
              :show-file-list="false" 
              name="file"
              class="upload-btn">
              <el-button type="primary" :icon="Plus">上传图片</el-button>
            </el-upload>
            <span class="upload-tip">建议上传清晰的商品图片，最多6张</span>
          </div>
          
          <div v-if="edit.images?.length > 0" class="image-preview-grid">
            <div v-for="(url, index) in edit.images" :key="index" class="image-preview-item">
              <img :src="url" class="preview-image" alt="商品图片"/>
              <div class="image-overlay">
                <el-button 
                  type="danger" 
                  circle 
                  size="small" 
                  @click="removeImage(index)"
                  :icon="Delete"/>
              </div>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="show=false" size="large">取消</el-button>
        <el-button type="primary" @click="saveEdit" size="large" :loading="loading">
          保存修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Plus, 
  Edit, 
  Delete, 
  Search, 
  Refresh, 
  Goods, 
  Sell, 
  View
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'

export default {
  name: 'ProfileMyProducts',
  components: {
    Plus,
    Edit,
    Delete,
    Search,
    Refresh,
    Goods,
    Sell,
    View
  },
  setup() {
    const router = useRouter()
    const items = ref([])
    const displayItems = ref([])
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
        available: allItems.filter(item => item.status === 'AVAILABLE').length,
        sold: allItems.filter(item => item.status === 'SOLD').length
      }
    })

    const fetch = async () => {
      try {
        const res = await api.get('/product/my')
        const d = res.data.data || {}
        items.value = (d.items || []).map(it => {
          let imgs = []
          try {
            const arr = Array.isArray(it.images) ? it.images : JSON.parse(it.images || '[]')
            imgs = arr.map(x => typeof x === 'string' ? x : (x?.url || x?.src || ''))
          } catch(e) {}
          return { ...it, images: imgs }
        })
        applyFilters()
      } catch (err) {
        console.error('Failed to fetch products:', err)
        ElMessage.error('获取商品列表失败')
      }
    }

    const onPageChange = (p) => {
      page.value = p
      applyFilters()
    }

    const getStatusText = (status) => {
      const statusMap = {
        'AVAILABLE': '未售出',
        'SOLD': '已售出'
      }
      return statusMap[status] || status
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

    const onImageUpload = (res) => {
      const url = res?.data?.data || res?.data || res
      if (url && edit.value.images.length < 6) {
        edit.value.images.push(url)
        ElMessage.success('图片上传成功')
      } else if (edit.value.images.length >= 6) {
        ElMessage.warning('最多只能上传6张图片')
      }
    }

    const removeImage = (index) => {
      edit.value.images.splice(index, 1)
      ElMessage.success('图片已移除')
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

    const getMainImage = (product) => {
      try {
        const imgs = product.images
        const arr = Array.isArray(imgs) ? imgs : JSON.parse(imgs || '[]')
        if (arr.length > 0) {
          const first = arr[0]
          if (typeof first === 'string') return first
          if (first && typeof first === 'object') return first.url || first.src || first.data || ''
        }
      } catch(e){}
      if (typeof product.images === 'string' && product.images.startsWith('/')) return product.images
      return ''
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

    const applyFilters = () => {
      let list = [...items.value]
      const kw = (searchQuery.value || '').trim().toLowerCase()
      if (kw) list = list.filter(it => (it.title || '').toLowerCase().includes(kw))
      if (statusFilter.value) list = list.filter(it => it.status === statusFilter.value)
      if (sortBy.value === 'newest') list.sort((a,b) => (b.created_at || 0) - (a.created_at || 0))
      else if (sortBy.value === 'price_asc') list.sort((a,b) => (a.price || 0) - (b.price || 0))
      else if (sortBy.value === 'price_desc') list.sort((a,b) => (b.price || 0) - (a.price || 0))
      else if (sortBy.value === 'views') list.sort((a,b) => (b.views || 0) - (a.views || 0))
      total.value = list.length
      const start = (page.value - 1) * size.value
      displayItems.value = list.slice(start, start + size.value)
    }
    const handleSearch = () => {
      page.value = 1
      applyFilters()
    }

    const handleFilter = () => {
      page.value = 1
      applyFilters()
    }

    const handleSort = () => {
      page.value = 1
      applyFilters()
    }

    const resetFilters = () => {
      searchQuery.value = ''
      statusFilter.value = ''
      sortBy.value = 'newest'
      page.value = 1
      applyFilters()
    }

    onMounted(() => {
      fetch()
    })

    return {
      items,
      displayItems,
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
      getStatusText,
      openEdit,
      onImageUpload,
      removeImage,
      saveEdit,
      getMainImage,
      del,
      handleSearch,
      handleFilter,
      handleSort,
      resetFilters,
      goDetail: (id) => { if (id) router.push(`/product/${id}`) },
      Plus,
      Edit,
      Delete,
      Search,
      Refresh,
      Goods,
      Sell,
      View
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
  font-weight: 600;
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
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
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
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
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
  transform: scale(1.05);
}

.no-image-placeholder {
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
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

.text-line {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
  padding: 16px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
}

.product-actions .el-button,
.action-btn {
  width: 120px !important;
  box-sizing: border-box;
  display: inline-flex;
  justify-content: center;
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