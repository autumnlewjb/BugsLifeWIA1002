<template>
  <h1>{{issue.title}}</h1>
  <p>Description: {{issue.descriptionText}}</p>
  <p>Priority: {{issue.priority}}</p>
  <p>Status: {{issue.status}}</p>
  <p>Tag: {{issue.tag}}</p>
  <p>Created by: {{issue.createdBy == null ? 'Not specified' : issue.createdBy}}</p>
  <p>Assignee: {{issue.assignee == null ? 'Not specified' : issue.assignee}}</p>
  <p>Timestamp: {{issue.timestamp == null ? 'Not Specified' : issue.timestamp}}</p>
  <div>
    <h2>Comments</h2>
    <div v-for="comment in issue.comment" :key="comment.id">
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