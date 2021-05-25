<template>
  <v-container>
    <v-container style="min-height: 80vh">
      <v-layout row justify-space-around>
        <v-flex xs12 md8>
          <h1 class="subheading">Project Dashboard</h1>
        </v-flex>
        <v-flex xs12 md2>
          <v-btn
            elevation="3"
            rounded
            color="teal"
            class="white--text"
            @click="dialog = true"
            >+ Add Project</v-btn
          >
        </v-flex>
      </v-layout>
      <v-layout row>
        <v-flex xs12 md12>
          <v-card
            v-for="project in getProjects"
            :key="project.id"
            class="pa-5 ma-5"
          >
            <v-layout row align-center>
              <v-card-title>{{ project.name }}</v-card-title>
              <v-card-text>{{ project.description }}</v-card-text>
              <v-card-text
                >Created on
                {{
                  project.date == null ? "(Not Specified)" : project.date
                }}</v-card-text
              >
              <v-card-actions class="d-flex justify-end">
                <v-btn
                  color="primary"
                  :to="{
                    path: 'project',
                    query: { projectId: project.project_id },
                  }"
                  text
                  >View Project</v-btn
                >
              </v-card-actions>
            </v-layout>
          </v-card>
        </v-flex>
      </v-layout>
    </v-container>
    <v-row justify="center">
      <v-dialog v-model="dialog" persistent max-width="600px">
        <ProjectForm @toggleDialog="toggleDialog" />
      </v-dialog>
    </v-row>
  </v-container>
</template>

<script>
import ProjectForm from "../components/ProjectForm";

export default {
  data() {
    return {
      hidden: false,
      dialog: false,
      projects: [],
    };
  },
  created() {
    this.fetchProjects();
  },
  components: {
    ProjectForm,
  },
  methods: {
    toggleDialog() {
      this.fetchProjects();
      this.dialog = false;
    },
    fetchProjects() {
      fetch(`/api/`)
        .then((res) => {
          if (res.status == 200) {
            return res.json();
          } else {
            return [];
          }
        })
        .then((data) => {
          this.projects = data;
        })
        .catch((e) => console.log(e));
    },
  },
  computed: {
    getProjects() {
      return this.projects;
    }
  }
};
</script>

<style scoped>
</style>