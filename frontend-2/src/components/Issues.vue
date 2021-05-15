<template>
  <div>
    <v-sheet elevation="10" class="ma-5 pa-5">
      <h1 class="subheading">Issue Dashboard</h1>
      <v-container>
        <v-layout row justify-center>
          <v-flex xs12 md12>
            <v-card v-for="issue in issues" :key="issue.id" class="">
              <v-card-title>
                {{ issue.title }}
                <v-chip-group class="px-3">
                  <v-chip>{{ issue.tag }}</v-chip>
                </v-chip-group>
              </v-card-title>
              <v-card-text>
                <v-icon v-for="n in issue.priority" :key="n" color="red">mdi-exclamation</v-icon>
              </v-card-text>
              <v-card-text
                >
                {{
                  issue.descriptionText == null
                    ? "(Description not specified)"
                    : issue.descriptionText
                }}</v-card-text
              >
              <v-card-text>
                Status: {{ issue.status }}<br/>
                Created on
                {{
                  project.date == null ? "(Not Specified)" : project.date
                }}
              </v-card-text>
              <v-card-actions>
                <v-btn text color="primary" :to="{name: 'Issue', query: {projectId: projectId, issueId: issue.issue_id}}">
                  View Issue
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-flex>
        </v-layout>
      </v-container>
    </v-sheet>
  </div>
</template>

<script>
export default {
  setup() {},
  data() {
    return {
      projectId: 0,
      issues: [],
      project: null,
    };
  },
  props: {
    data: Object,
  },
  created() {
    this.projectId = this.$route.query.projectId;
    this.project = this.data["project"].find(
      (project) => project.project_id == this.projectId
    );
    this.issues = this.project["issue"];
  },
};
</script>

<style scoped>
</style>