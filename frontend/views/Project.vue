<template>
  <h1>{{project["name"]}}</h1>
  <span><router-link :to="{path: '/issues', query: {projectId: projectId}}">Issues</router-link></span>
</template>

<script>
export default {
  data() {
    return {
      projectId: 0,
      project: null,
    } 
  },
  setup() {},
  props: {
    data: JSON
  },
  created() {
    this.projectId = this.$route.query.projectId,
    this.project = this.data["project"].find((project) => project.project_id == this.projectId)
  }
};
</script>

<style scoped>
</style>