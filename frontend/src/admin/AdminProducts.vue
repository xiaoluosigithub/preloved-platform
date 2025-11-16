<template>
  <div class="admin-page">
    <div class="tools">
      <el-select v-model="status" placeholder="状态" style="width:140px">
        <el-option label="全部" value="" />
        <el-option label="PENDING" value="PENDING" />
        <el-option label="AVAILABLE" value="AVAILABLE" />
        <el-option label="SOLD" value="SOLD" />
      </el-select>
      <el-input v-model="keyword" placeholder="关键词" style="width:240px" />
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <el-table :data="list" style="width:100%" row-key="id">
      <el-table-column type="expand">
        <template #default="{ row }">
          <div class="orders">
            <el-table :data="orders[row.id] || []" style="width:100%">
              <el-table-column prop="orderNo" label="订单号" width="140" />
              <el-table-column prop="status" label="状态" width="120" />
              <el-table-column prop="price" label="成交价" width="120" />
              <el-table-column prop="buyerUsername" label="买家" width="140" />
              <el-table-column prop="sellerUsername" label="卖家" width="140" />
              <el-table-column prop="createdAt" label="创建时间" width="180" />
              <el-table-column prop="shippedAt" label="发货时间" width="180" />
              <el-table-column prop="receivedAt" label="收货时间" width="180" />
              <el-table-column prop="receiverName" label="收件人" width="140" />
              <el-table-column prop="receiverPhone" label="电话" width="140" />
              <el-table-column prop="shippingNo" label="物流单号" width="160" />
              <el-table-column prop="shippingCompany" label="物流公司" width="160" />
            </el-table>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="price" label="价格" width="120" />
      <el-table-column prop="status" label="商品状态" width="140" />
      <el-table-column prop="createdAt" label="发布于" width="180" />
      <el-table-column label="操作" width="220">
        <template #default="{row}">
          <el-button v-if="row.status==='PENDING'" size="small" type="success" @click="approve(row.id)">通过</el-button>
          <el-button v-if="row.status==='PENDING'" size="small" type="danger" @click="reject(row.id)">驳回</el-button>
          <el-button size="small" @click="toggleOrders(row.id)">订单明细</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pager">
      <el-pagination :current-page="page" :page-size="size" :total="total" layout="prev, pager, next" @current-change="onPage" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api'
import { useRouter } from 'vue-router'

const router = useRouter()
const list = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const status = ref('')
const keyword = ref('')
const orders = ref({})

const ensureAdmin = async () => {
  const token = localStorage.getItem('token')
  if (!token) { router.push('/admin/login'); return false }
  const { data } = await api.get('/admin/ping')
  if (data.code !== 0) { router.push('/admin/login'); return false }
  return true
}

const load = async () => {
  const ok = await ensureAdmin(); if (!ok) return
  const params = { page: page.value, size: size.value }
  if (status.value) params.status = status.value
  if (keyword.value) params.keyword = keyword.value
  const { data } = await api.get('/admin/products', { params })
  if (data.code !== 0) return
  list.value = data.data.list || []
  total.value = data.data.total || 0
}

const onPage = (p) => { page.value = p; load() }

const approve = async (id) => {
  const { data } = await api.post(`/admin/products/${id}/approve`)
  if (data.code === 0) load()
}
const reject = async (id) => {
  const { data } = await api.post(`/admin/products/${id}/reject`)
  if (data.code === 0) load()
}

const toggleOrders = async (productId) => {
  if (!orders.value[productId]) {
    const { data } = await api.get(`/admin/products/${productId}/orders`)
    if (data.code === 0) orders.value[productId] = data.data || []
  } else {
    delete orders.value[productId]
    orders.value = { ...orders.value }
  }
}

onMounted(load)
</script>

<style scoped>
.admin-page { padding: 16px; }
.tools { display:flex; align-items:center; gap:8px; margin-bottom:12px; }
.pager { margin-top: 12px; display:flex; justify-content:center; }
</style>