<template>
  <h1>username's Project Dashboard</h1>
  <table style="border: 1px">
    <tr>
      <th>Project ID</th>
      <th>Project Name</th>
    </tr>
    <tr v-for="project in projects" :key="project.id">
      <td>{{project.id}}</td>
      <td>
        <router-link :to="{ path: '/project', query: { projectId: project.id } }">{{project.name}}</router-link>
      </td>
    </tr>
  </table>
</template>

<script>

export default {
  setup() {},
  data() {
    return {
      projects: this.data['projects']
    }
  },
  props: {
    data: JSON
  }
};
</script>

<style scoped>
</style>