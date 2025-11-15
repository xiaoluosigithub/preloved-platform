<template>
  <InteractList 
    title="我的收藏"
    endpoint="/product-interact/my-favorite"
    toggle-action="favorite"
    action-text="取消收藏"
  />
</template>

<script>
import InteractList from '@/components/profile/InteractList.vue'
export default { components:{ InteractList } }
</script>