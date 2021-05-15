<template>
  <div>
    <h1 class="subheading">Project Dashboard</h1>
    <v-container>
      <v-layout row justify-center>
        <v-flex xs12 md12>
          <v-card
            v-for="project in projects"
            :key="project.id"
            class="pa-5 ma-5"
          >
            <v-layout row align-center>
              <v-card-title>{{ project.name }}</v-card-title>
              <v-card-text>{{ project.description }}</v-card-text>
              <v-card-text>Created on {{ project.date == null ? '(Not Specified)' : project.date }}</v-card-text>
              <v-card-actions>
                <v-btn outlined color="normal" :to="{path: 'project', query: {projectId: project.project_id}}">View Issue</v-btn>
              </v-card-actions>
            </v-layout>
          </v-card>
        </v-flex>
      </v-layout>
    </v-container>
  </div>
</template>

<script>
export default {
  setup() {},
  data() {
    return {
      user: this.data,
      projects: this.data["project"],
    };
  },
  created() {
    console.log("created");
  },
  props: {
    data: Object,
  },
};
</script>

<style scoped>
</style>