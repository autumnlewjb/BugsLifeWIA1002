<template>
  <div class="comment">
    <h3>{{comment.text}}</h3>
    <p>authored by: {{comment.user}}</p>
  </div>
</template>

<script>
export default {
  setup() {
    
  },
  props: {
    comment: Object
  }
}
</script>

<style scoped>

</style>