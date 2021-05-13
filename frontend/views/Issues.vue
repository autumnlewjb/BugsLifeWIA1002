<template>
  <h1>Issues for {{project.name}}</h1>
  <table>
    <tr>
      <th>Issue ID</th>
      <th>Issue Name</th>
    </tr>
    <tr v-for="issue in issues" :key="issue.id">
      <td>{{issue.issue_id}}</td>
      <td>
        <router-link :to="{path: '/issue', query: {projectId: projectId, issueId: issue.issue_id}}">{{issue.title}}</router-link>
      </td>
    </tr>
  </table>
</template>

<script>
export default {
  setup() {},
  data() {
    return {
      projectId: 0,
      issues: [],
      project: null
    }
  },
  props: {
    data: JSON
  },
  created() {
    this.projectId = this.$route.query.projectId
    this.project = this.data['project'].find((project) => project.project_id == this.projectId)
    this.issues = this.project['issue']
  }
};
</script>

<style scoped>
</style>