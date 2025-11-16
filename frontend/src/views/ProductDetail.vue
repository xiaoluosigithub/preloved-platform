<template>
  <div style="max-width:900px;margin:20px auto">
    <el-button type="primary" @click="goBack" class="back-inline">返回</el-button>
    <el-card class="detail-card">
      <el-row :gutter="20">
        <el-col v-if="images.length > 0" :span="10">
          <el-carousel height="260px" indicator-position="outside" :autoplay="false">
            <el-carousel-item v-for="(u,i) in images" :key="i">
              <img :src="u" class="carousel-image"/>
            </el-carousel-item>
          </el-carousel>
        </el-col>
        <el-col :span="images.length > 0 ? 14 : 24">
          <div class="info-block">
            <div class="info-item"><span class="label">商品名称：</span><span>{{ product.title }}</span></div>
            <div class="info-item"><span class="label">商品价格：</span><span>¥ {{ product.price }}</span></div>
            <div class="info-item">
              <span class="label">商品详情：</span>
              <div class="desc" v-html="product.description"></div>
            </div>
            <div class="actions">
              <el-button link @click="toggleLike">👍 {{ likeCount }} {{ isLiked ? '(已点赞)' : '' }}</el-button>
              <el-button link @click="toggleFavorite">⭐ {{ favorCount }} {{ isFavorited ? '(已收藏)' : '' }}</el-button>
              <el-button type="primary" @click="goBuy" :disabled="product.status!=='AVAILABLE'">立即购买</el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="comments-card">
      <div class="comment-editor">
        <el-input type="textarea" v-model="commentText" placeholder="写评论..." :rows="3"></el-input>
        <el-button type="primary" @click="postComment" class="submit-comment">发表评论</el-button>
      </div>

      <div class="comments-section">
        <el-divider>评论 ({{ commentsTotal }})</el-divider>
        <div v-for="c in comments" :key="c.id" class="comment-item">
          <img :src="c.avatar || defaultAvatar" class="comment-avatar" />
          <div class="comment-content">
            <div class="comment-author">{{ c.nickname || c.username }}</div>
            <div class="comment-text">{{ c.content }}</div>
            <div class="comment-time">{{ c.created_at }}</div>
          </div>
        </div>
        <el-pagination background layout="prev, pager, next" :page-size="commentsSize" :current-page="commentsPage" :total="commentsTotal" @current-change="onCommentsPageChange" />
      </div>
    </el-card>
  </div>
</template>

<script>
import api from '@/api'
import { ElMessageBox } from 'element-plus'
export default {
  props: ['id'],
  data(){ return { product:{}, images:[], comments:[], commentText:'', isFavorited:false, favorCount:0, isLiked:false, likeCount:0, commentsPage:1, commentsSize:10, commentsTotal:0, defaultAvatar:'https://via.placeholder.com/36' } },
  async mounted(){
    const pid = this.$route.params.id || this.id
    const res = await api.get(`/products/${pid}`)
    const d = res.data.data || {}
    this.product = d.product || d || {}
    try {
      const imgs = this.product.images
      const arr = Array.isArray(imgs) ? imgs : JSON.parse(imgs || '[]')
      this.images = arr.map(x => (typeof x === 'string') ? x : (x?.url || x?.src || x?.data || '')).filter(Boolean)
    } catch(e){
      this.images = (typeof this.product.images === 'string' && this.product.images.startsWith('/')) ? [this.product.images] : []
    }
    this.isFavorited = !!(d.favorited)
    this.isLiked = !!(d.liked)
    this.favorCount = d.favCount ?? d.favoriteCount ?? 0
    this.likeCount = d.likeCount ?? 0
    await this.fetchComments()
  }
  ,methods:{
    async fetchComments(){
      if (!this.product.id) return
      const res = await api.get(`/products/${this.product.id}/comments`, { params:{ page:this.commentsPage, size:this.commentsSize }})
      const d = res.data.data || {}
      this.comments = d.items || []
      this.commentsTotal = d.total || 0
    },
    onCommentsPageChange(p){ this.commentsPage = p; this.fetchComments() },
    async toggleFavorite(){
      const token = localStorage.getItem('token')
      if (!token) { this.$message.warning('未登录请先登录'); this.$router.push('/login'); return }
      try {
        const res = await api.post(`/products/${this.product.id}/favorite`)
        const d = res.data.data || {}
        this.isFavorited = !!d.favorited
        this.favorCount = d.count ?? this.favorCount
      } catch(e){ this.$message.error(e.response?.data?.msg || '操作失败') }
    },
    async toggleLike(){
      const token = localStorage.getItem('token')
      if (!token) { this.$message.warning('未登录请先登录'); this.$router.push('/login'); return }
      try {
        const res = await api.post(`/products/${this.product.id}/like`)
        const d = res.data.data || {}
        this.isLiked = !!d.liked
        this.likeCount = d.count ?? this.likeCount
      } catch(e){ this.$message.error(e.response?.data?.msg || '操作失败') }
    },
    async postComment(){
      const token = localStorage.getItem('token')
      if (!token) { this.$message.warning('未登录请先登录'); this.$router.push('/login'); return }
      if(!this.commentText || !this.commentText.trim()) { this.$message.warning('请输入评论内容'); return }
      try {
        await api.post(`/products/${this.product.id}/comments`, { content: this.commentText })
        this.commentText = ''
        this.$message.success('评论成功')
        await this.fetchComments()
      } catch(e){ this.$message.error(e.response?.data?.msg || '评论失败') }
    },
    goBack(){
      if (window.history.length > 1) this.$router.back()
      else this.$router.push('/')
    },
    goBuy(){
      const token = localStorage.getItem('token')
      if (!token) { this.$message.warning('未登录请先登录'); this.$router.push('/login'); return }
      const u = localStorage.getItem('user')
      const me = u ? JSON.parse(u) : null
      const buyerId = Number(me?.id)
      const sellerId = Number(this.product?.sellerId)
      if (!Number.isNaN(buyerId) && !Number.isNaN(sellerId) && buyerId === sellerId) { ElMessageBox.alert('不能购买自己发布的商品', '提示'); return }
      this.$router.push('/order/buy/'+this.product.id)
    }
  }
}
</script>

<style scoped>
.back-inline { margin-bottom: 8px }
.detail-card { padding: 12px }
.comments-card { margin-top: 16px; padding: 12px }
.carousel-image { width: 100%; height: 100%; object-fit: cover; border-radius: 8px }
.info-block { display: flex; flex-direction: column; gap: 10px }
.info-item { display: flex; gap: 8px; align-items: flex-start }
.label { color: var(--text-secondary); min-width: 90px }
.desc { color: #333; line-height: 1.6 }
.actions { margin-top: 8px; display: flex; gap: 8px; align-items: center; flex-wrap: wrap }
.comment-editor { margin-top: 20px }
.submit-comment { margin-top: 8px }
.comments-section { margin-top: 20px }
.comment-item { display: flex; gap: 10px; padding: 12px; border: 1px solid #e5e7eb; border-radius: 8px; margin-bottom: 12px; background: #fff }
.comment-avatar { width: 36px; height: 36px; border-radius: 50% }
.comment-content { max-width: 700px }
.comment-author { font-weight: 600 }
.comment-text { background:#f5f7fa; border-radius:8px; padding:8px 12px; color:#333; line-height:1.6; margin-top:4px }
.comment-time { color:#999; font-size:12px; margin-top:6px }
</style>