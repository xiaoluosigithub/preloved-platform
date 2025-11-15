<template>
  <div style="max-width:1000px;margin:20px auto">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px">
      <h3>卖家订单列表</h3>
    </div>
    <el-table :data="items" style="width:100%">
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="productTitle" label="商品" />
      <el-table-column prop="price" label="成交价" width="120" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
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
  methods:{ async fetch(){ const res = await api.get('/order/sell'); this.items = res.data.data || [] } }
}
</script>