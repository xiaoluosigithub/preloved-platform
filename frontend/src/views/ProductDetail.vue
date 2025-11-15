<template>
  <div style="max-width:900px;margin:20px auto">
    <el-card>
      <h2>{{ product.title }}</h2>
      <div>¥ {{ product.price }}</div>
      <div style="margin:12px 0">
        <img v-for="(u,i) in images" :key="i" :src="u" style="width:160px;margin-right:8px"/>
      </div>
      <div v-html="product.description"></div>

      <div style="margin-top:12px">
        <el-button type="text" @click="toggleLike">👍 {{ likeCount }} {{ isLiked ? '(已点赞)' : '' }}</el-button>
        <el-button type="text" @click="toggleFavorite">⭐ {{ favorCount }} {{ isFavorited ? '(已收藏)' : '' }}</el-button>
      </div>

      <div style="margin-top:20px">
        <el-input type="textarea" v-model="commentText" placeholder="写评论..." rows="3"></el-input>
        <el-button type="primary" @click="postComment" style="margin-top:8px">发表评论</el-button>
      </div>

      <div style="margin-top:20px">
        <el-divider>评论 ({{ commentsTotal }})</el-divider>
        <div v-for="c in comments" :key="c.id" style="padding:8px 0;border-bottom:1px solid #f0f0f0">
          <div style="font-weight:600">{{ c.nickname || c.username }}</div>
          <div style="color:#666;font-size:13px">{{ c.content }}</div>
          <div style="color:#999;font-size:12px">{{ c.created_at }}</div>
        </div>
        <el-pagination background layout="prev, pager, next" :page-size="commentsSize" :current-page="commentsPage" :total="commentsTotal" @current-change="onCommentsPageChange" />
      </div>
    </el-card>
  </div>
</template>

<script>
import api from '@/api'
export default {
  props: ['id'],
  data(){ return { product:{}, images:[], comments:[], commentText:'', isFavorited:false, favorCount:0, isLiked:false, likeCount:0, commentsPage:1, commentsSize:10, commentsTotal:0 } },
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
      try {
        const res = await api.post(`/products/${this.product.id}/favorite`)
        const d = res.data.data || {}
        this.isFavorited = !!d.favorited
        this.favorCount = d.count ?? this.favorCount
      } catch(e){ this.$message.error(e.response?.data?.msg || '操作失败') }
    },
    async toggleLike(){
      try {
        const res = await api.post(`/products/${this.product.id}/like`)
        const d = res.data.data || {}
        this.isLiked = !!d.liked
        this.likeCount = d.count ?? this.likeCount
      } catch(e){ this.$message.error(e.response?.data?.msg || '操作失败') }
    },
    async postComment(){
      if(!this.commentText || !this.commentText.trim()) { this.$message.warning('请输入评论内容'); return }
      try {
        await api.post(`/products/${this.product.id}/comments`, { content: this.commentText })
        this.commentText = ''
        this.$message.success('评论成功')
        await this.fetchComments()
      } catch(e){ this.$message.error(e.response?.data?.msg || '评论失败') }
    }
  }
}
</script>