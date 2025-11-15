<template>
  <InteractList 
    title="我的点赞"
    endpoint="/product-interact/my-like"
    toggle-action="like"
    action-text="取消点赞"
  />
</template>

<script>
import InteractList from '@/components/profile/InteractList.vue'
export default { components:{ InteractList } }
</script>