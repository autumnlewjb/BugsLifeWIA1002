<template>
  <h1>{{user.username}}'s Project Dashboard</h1>
  <p>Username: {{user.username}}</p>
  <p>Email: {{user.email}}</p>
  <table style="border: 1px">
    <tr>
      <th>Project ID</th>
      <th>Project Name</th>
    </tr>
    <tr v-for="project in projects" :key="project.id">
      <td>{{project.project_id}}</td>
      <td>
        <router-link :to="{ path: '/project', query: { projectId: project.project_id } }">{{project.name}}</router-link>
      </td>
    </tr>
  </table>
</template>

<script>

export default {
  setup() {},
  data() {
    return {
      user: this.data,
      projects: this.data['project']
    }
  },
  props: {
    data: JSON
  }
};
</script>

<style scoped>
</style>