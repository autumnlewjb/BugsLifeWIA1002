<template>
  <div class="comment">
    <v-card class="ma-2" outlined>
        <v-card-text class="text--body-1 black--text blue-grey lighten-4"><strong>Commented on {{comment.timestamp != null ? ' ' + comment.timestamp : "unknown time"}}</strong></v-card-text>
        <v-card-text class="text--body-1 black--text">{{comment.text}}</v-card-text>
    </v-card>
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