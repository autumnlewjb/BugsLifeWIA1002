<template>
<v-container>
  <v-layout row>
    <v-flex xs8 md8>
      <v-container>
        <h1>{{getProject.name}}
          <v-btn class="mx-0" plain @click="editProject">Edit</v-btn>
          <v-btn class="mx-0" plain color="red" @click="toggleDeleteDialog(false)">Delete</v-btn>
        </h1>
        <p>Description: {{getProject.description}}</p>
        <p>Date created: {{project.date == null ? "Not specified" : getProject.date}}</p>
        
      </v-container>
    </v-flex>
  </v-layout>
  <Issues :data="data"></Issues>
  
    <v-dialog
      v-model="dialog"
      persistent
      max-width="600px"
    >
      <ProjectForm :projectId="projectId" @toggleDialog="toggleDialog" :data="data"/>
    </v-dialog>
    <ConfirmDelete @toggleDeleteDialog="toggleDeleteDialog" :showDialog="confirmDeleteDialog" />
</v-container>
  
</template>

<script>
import Issues from '../views/Issues'
import ProjectForm from "../components/ProjectForm"
import ConfirmDelete from "../components/ConfirmDelete"

export default {
  data() {
    return {
      projectId: 0,
      project: null,
      showIssues: false,
      dialog: false,
      confirmDeleteDialog: false
    } 
  },
  setup() {},
  props: {
    data: Object
  },
  created() { 
    console.log(this.$store.getters.getProjects)
    this.projectId = this.$route.query.projectId
    this.project = this.$store.getters.getCurrentUser.project.find((project) => project.project_id == this.projectId)
  },
  mounted() {},
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
      fetch(`/api/${this.$store.getters.getUsername}/${this.projectId}`,{
        method: 'DELETE'
      })
      .then((res) => {
        if (res.status == 200) {
          console.log("delete successful")
          this.$store.dispatch('fetchCurrentUser')
          this.$router.push({name: 'Projects'})
        } else {
          console.log("delete unsuccessful")
        }
      })
      .catch((e) => console.log(e));
    },
    toggleDialog() {
      this.dialog = !this.dialog
    },
    toggleDeleteDialog(userResponse) {
      this.confirmDeleteDialog = !this.confirmDeleteDialog;
      if (userResponse) {
        this.deleteProject();
      }
    }
  },
  components: {
    Issues,
    ProjectForm,
    ConfirmDelete
  },
  computed: {
    getProject() {
      return this.$store.getters.getCurrentUser.project.find((project) => project.project_id == this.projectId)
    }
  }
};
</script>

<style scoped>
</style>