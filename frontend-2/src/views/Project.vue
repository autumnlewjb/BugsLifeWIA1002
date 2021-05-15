<template>
<div>
  <div>
    <v-breadcrumbs
    :items="items">
      
    </v-breadcrumbs>
  </div>
<h1>{{project.name}}</h1>
  <p>Description: {{project.description}}</p>
  <p>Date created: {{project.date == null ? "Not specified" : project.date}}</p>
  <v-btn @click="toggleIssues">{{showIssues ? 'Close' : 'See All Issues'}}</v-btn>
  <Issues v-if="showIssues" :data="data"></Issues>
</div>
  
</template>

<script>
import Issues from '../components/Issues'

export default {
  data() {
    return {
      projectId: 0,
      project: null,
      showIssues: false,
      items: [],
    } 
  },
  setup() {},
  props: {
    data: Object
  },
  created() { 
    this.projectId = this.$route.query.projectId
    console.log(this.data) 
    console.log(this.projectId)
    this.project = this.data["project"].find((project) => project.project_id == this.projectId)
    this.items.push({
          text: this.project.name,
          disabled: false,
          href: window.location.href,
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
  }
};
</script>

<style scoped>
</style>