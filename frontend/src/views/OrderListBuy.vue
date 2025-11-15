<template>
  <div style="max-width:1000px;margin:20px auto">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px">
      <h3>我的订单（买家）</h3>
    </div>
    <el-table :data="items" style="width:100%">
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="productTitle" label="商品" />
      <el-table-column prop="productPrice" label="原价" width="120" />
      <el-table-column prop="price" label="成交价" width="120" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <el-button v-if="scope.row.status==='PENDING'" size="small" type="primary" @click="pay(scope.row)">去支付</el-button>
          <el-button v-if="scope.row.status==='PAID'" size="small" type="success" @click="received(scope.row)">确认收货</el-button>
          <el-button size="small" @click="$router.push('/product/'+scope.row.productId)">查看商品</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import api from '@/api'
export default {
  data(){ return { items:[] } },
  mounted(){ this.fetch() },
  methods:{
    async fetch(){ const res = await api.get('/order/buy'); this.items = res.data.data || [] },
    async pay(row){ try { await api.post(`/order/pay/${row.id}`); this.$message.success('已支付'); this.fetch() } catch(e){ this.$message.error(e.response?.data?.msg || '支付失败') } },
    async received(row){ try { await api.post(`/order/received/${row.id}`); this.$message.success('已收货'); this.fetch() } catch(e){ this.$message.error(e.response?.data?.msg || '操作失败') } }
  }
}
</script>