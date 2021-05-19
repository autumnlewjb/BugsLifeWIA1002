<template>
<v-container>
  <v-layout row>
    <v-flex xs8 md8>
      <v-container>
        <h1>{{project.name}}</h1>
        <p>Description: {{project.description}}</p>
        <p>Date created: {{project.date == null ? "Not specified" : project.date}}</p>
        
      </v-container>
    </v-flex>
    <v-flex xs2 md2 style="text-align: end;" align-self-center>
      <v-menu offset-y>
        <template v-slot:activator="{ on, attrs }">
          <v-btn
            color="primary"
            dark
            plain
            v-bind="attrs"
            v-on="on"
          >
            Actions
          </v-btn>
        </template>
        <v-list>
          <v-list-item @click="editProject">
            <v-list-item-title>
              Edit project
            </v-list-item-title>
          </v-list-item>
          <v-list-item @click="deleteProject">
            <v-list-item-title>
              Delete project
            </v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-flex>
  </v-layout>
  <Issues :data="data" @updateUserData="$emit('updateUserData')"></Issues>
  
    <v-dialog
      v-model="dialog"
      persistent
      max-width="600px"
    >
      <ProjectForm :projectId="projectId" @toggleDialog="toggleDialog" @updateUserData="$emit('updateUserData')" :data="data"/>
    </v-dialog>
</v-container>
  
</template>

<script>
import Issues from '../views/Issues'
import ProjectForm from "../components/ProjectForm"

export default {
  data() {
    return {
      projectId: 0,
      project: null,
      showIssues: false,
      dialog: false
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
    },
    editProject() {
      this.dialog = true
    },
    deleteProject() {
      console.log("delete project")
    },
    toggleDialog() {
      this.dialog = !this.dialog
    }
  },
  components: {
    Issues,
    ProjectForm
  },
};
</script>

<style scoped>
</style>