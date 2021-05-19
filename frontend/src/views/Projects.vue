<template>
  <v-container>
    <v-container style="min-height: 80vh">
      <v-layout row justify-space-around>
        <v-flex xs12 md8>
          <h1 class="subheading">Project Dashboard</h1>
        </v-flex>
        <v-flex xs12 md2>
          <v-btn elevation="3" rounded color="teal" class="white--text" @click="dialog=true">+ Add Project</v-btn>
        </v-flex>
      </v-layout>
      <v-layout row justify-center>
        <v-flex xs12 md12>
          <v-card
            v-for="project in getProjects"
            :key="project.id"
            class="pa-5 ma-5"
          >
            <v-layout row align-center>
              <v-card-title>{{ project.name }}</v-card-title>
              <v-card-text>{{ project.description }}</v-card-text>
              <v-card-text>Created on {{ project.date == null ? '(Not Specified)' : project.date }}</v-card-text>
              <v-card-actions>
                <v-btn outlined color="normal" :to="{path: 'project', query: {projectId: project.project_id}}">View Issues</v-btn>
              </v-card-actions>
            </v-layout>
          </v-card>
        </v-flex>
      </v-layout>
    </v-container>
    <v-row justify="center">
    <v-dialog
      v-model="dialog"
      persistent
      max-width="600px"
    >
      <ProjectForm @toggleDialog="toggleDialog" @updateUserData="$emit('updateUserData')" :data="data"/>
    </v-dialog>
  </v-row>
  </v-container>
</template>

<script>
import ProjectForm from '../components/ProjectForm'
export default {
  setup() {},
  data() {
    return {
      hidden: false,
      dialog: false
    };
  },
  created() {
    // this.$store.dispatch('fetchCurrentUser')
  },
  props: {
    data: Object,
  },
  components: {
    ProjectForm
  },
  methods: {
    toggleDialog() {
      this.dialog = false
    }
  },
  computed: {
    getProjects() {
      if (this.$store.getters.getCurrentUser) {
        return this.$store.getters.getCurrentUser.project;
      } else {
        return [];
      }
    }
  }
};
</script>

<style scoped>
</style>