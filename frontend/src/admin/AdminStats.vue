<template>
  <div class="admin-page">
    <div class="tools">
      <el-button type="primary" @click="load">刷新</el-button>
    </div>
<div id="chart" style="height:360px; margin-bottom:16px;"></div>
    <el-table :data="points" style="width:100%">
      <el-table-column prop="day" label="日期" width="160" />
      <el-table-column prop="cnt" label="订单数" width="120" />
      <el-table-column prop="amount" label="销售额" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onBeforeUnmount } from 'vue'
import api from '@/api'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'

const router = useRouter()
const points = ref([])
let chart

const ensureAdmin = async () => {
  const token = localStorage.getItem('token')
  if (!token) { router.push('/admin/login'); return false }
  const { data } = await api.get('/admin/ping')
  if (data.code !== 0) { router.push('/admin/login'); return false }
  return true
}

const load = async () => {
  const ok = await ensureAdmin(); if (!ok) return
  const { data } = await api.get('/admin/stats/orders')
  if (data.code !== 0) return
  points.value = data.data.points || []
  await nextTick()
  renderChart()
}

onMounted(load)

const renderChart = () => {
  const el = document.getElementById('chart')
  if (!el) return
  if (!chart) chart = echarts.init(el)
  const days = points.value.map(p => p.day)
  const cnts = points.value.map(p => Number(p.cnt || 0))
  const amts = points.value.map(p => Number(p.amount || 0))
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['订单数', '销售额'] },
    xAxis: { type: 'category', data: days },
    yAxis: [ { type: 'value', name: '订单数' }, { type: 'value', name: '销售额' } ],
    series: [
      { name: '订单数', type: 'line', data: cnts },
      { name: '销售额', type: 'line', yAxisIndex: 1, data: amts }
    ]
  })
}

onBeforeUnmount(() => { if (chart) { chart.dispose(); chart = null } })
</script>

<style scoped>
.admin-page { padding: 16px; }
.tools { display:flex; gap:8px; align-items:center; margin-bottom:12px; }
</style>