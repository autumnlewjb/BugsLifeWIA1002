<template>
<v-container>
  <v-container>
    <h1>{{project.name}}</h1>
    <p>Description: {{project.description}}</p>
    <p>Date created: {{project.date == null ? "Not specified" : project.date}}</p>
    <v-btn @click="toggleIssues">{{showIssues ? 'Close' : 'See All Issues'}}</v-btn>
    <Issues v-if="showIssues" :data="data" @updateUserData="$emit('updateUserData')"></Issues>
  </v-container>
</v-container>
  
</template>

<script>
import Issues from '../components/Issues'

export default {
  data() {
    return {
      projectId: 0,
      project: null,
      showIssues: false,
    } 
  },
  setup() {},
  props: {
    data: Object
  },
  created() { 
    this.projectId = this.$route.query.projectId
    this.project = this.$store.getters.getCurrentUser.project.find((project) => project.project_id == this.projectId)
  },
  mounted() {
    this.$emit('addToBreadcrumb', {
      text: this.project.name,
      disabled: false,
      href: window.location.href
    })
  },
  methods: {
    toggleIssues() {
      if (this.$vuetify.breakpoint.mdAndUp) {
        this.showIssues = !this.showIssues
      } else {
        this.$router.push({name: 'Issues', query: {projectId: this.projectId}})
      }
    }
  },
  components: {
    Issues
  },
};
</script>

<style scoped>
</style>