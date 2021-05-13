<template>
  <h1>Issues for {{project.name}}</h1>
  <table>
    <tr>
      <th>Issue ID</th>
      <th>Issue Name</th>
    </tr>
    <tr v-for="issue in issues" :key="issue.id">
      <td>{{issue.id}}</td>
      <td>
        <router-link :to="{path: '/issue', query: {projectId: projectId, issueId: issue.id}}">{{issue.title}}</router-link>
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
    this.project = this.data['projects'].find((project) => project.id == this.projectId)
    this.issues = this.project['issues']
  }
};
</script>

<style scoped>
</style>