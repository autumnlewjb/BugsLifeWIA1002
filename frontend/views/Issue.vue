<template>
  <h1>{{issue.title}}</h1>
  <p>{{issue.descriptionText}}</p>
  <div>
    <h2>Comments</h2>
    <div v-for="comment in issue.comments" :key="comment.id">
      <Comment :comment="comment"/>
    </div>
  </div>
</template>

<script>
import Comment from '../src/components/Comment'
export default {
  setup() {},
  data() {
    return {
      projectId: 0,
      issueId: 0,
      project: null,
      issue: null
    }
  },
  created() {
    this.projectId = this.$route.query.projectId
    this.issueId = this.$route.query.issueId
    this.project = this.data["project"].find((project) => project.project_id == this.projectId)
    this.issue = this.project["issue"].find((issue) => issue.issue_id == this.issueId)
  },
  props: {
    data: JSON
  },
  components: {
    Comment
  }
};
</script>

<style scoped>
</style>