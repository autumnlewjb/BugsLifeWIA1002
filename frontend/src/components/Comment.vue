<template>
  <div class="comment">
    <v-card class="ma-2" outlined>
        <v-card-text class="text--body-1 black--text blue-grey lighten-4"><strong>Commented on {{comment.timestamp != null ? ' ' + comment.timestamp : "unknown time"}}</strong></v-card-text>
        <v-card-text class="text--body-1 black--text">{{comment.text}}</v-card-text>
        <v-card-text>
          <v-chip @click="addEmoji('happy')" class="ma-1">
            &#128520; {{happy}}
          </v-chip>
          <v-chip @click="addEmoji('angry')" class="ma-1">
            &#128516; {{angry}}
          </v-chip>
          <v-chip @click="addEmoji('thumbsup')" class="ma-1">
            &#128077; {{thumbsup}}
          </v-chip>
        </v-card-text>
    </v-card>
  </div>
</template>

<script>
export default {
  setup() {
    
  },
  data() {
    return {
      angry: this.comment.react.find((r) => r.reaction === "angry") ? this.comment.react.find((r) => r.reaction === "angry").count : 0,
      happy: this.comment.react.find((r) => r.reaction === "happy") ? this.comment.react.find((r) => r.reaction === "happy").count : 0,
      thumbsup: this.comment.react.find((r) => r.reaction === "thumbsup") ? this.comment.react.find((r) => r.reaction === "thumbsup").count : 0
    }
  },
  props: {
    comment: Object
  },
  methods: {
    addEmoji(str) {
      if (str == 'happy') {
        this.happy ++;
      } else if (str == 'angry') {
        this.angry ++;
      } else {
        this.thumbsup ++;
      }
    }
  }
}
</script>

<style scoped>

</style>