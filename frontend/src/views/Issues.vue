<template>
  <div>
    <v-sheet elevation="10" class="ma-5 pa-5" rounded>
      <v-container>
        <v-layout row>
          <v-flex xs12 md8>
            <h1 class="subheading">Issue Dashboard</h1>
          </v-flex>
          <v-flex xs12 md2 class="d-flex justify-end">
            <v-btn
              elevation="3"
              rounded
              color="teal"
              class="white--text"
              @click="dialog = true"
              >+ Add Issue</v-btn
            >
          </v-flex>
        </v-layout>
      </v-container>
      <v-container>
        <v-layout row justify-center>
          <v-flex xs12 md12>
            <v-card v-for="issue in getIssues" :key="issue.id" class="ma-5">
              <v-card-title>
                {{ issue.title }}
                <v-icon v-for="n in issue.priority" :key="n" color="red"
                  >mdi-exclamation</v-icon
                >
              </v-card-title>
              <v-card-text>
                <v-chip-group>
                  <v-chip v-for="tag in issue.tag" :key="tag" class="mx-1">{{ tag }}</v-chip>
                </v-chip-group>
              </v-card-text>
              <v-card-text>
                {{
                  issue.descriptionText == null
                    ? "(Description not specified)"
                    : issue.descriptionText
                }}</v-card-text
              >
              <v-card-text>
                Status: {{ issue.status }}<br />
                Created on
                {{ issue.timestamp == null ? "(Not Specified)" : issue.timestamp }}
              </v-card-text>
              <v-card-actions>
                <v-btn
                  text
                  color="primary"
                  :to="{
                    name: 'Issue',
                    query: { projectId: projectId, issueId: issue.issue_id },
                  }"
                >
                  View Issue
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-flex>
        </v-layout>
      </v-container>
      <v-dialog v-model="dialog" persistent max-width="600px">
        <IssueForm
          @toggleDialog="toggleDialog"
          :data="data"
          :projectId="projectId"
        />
      </v-dialog>
    </v-sheet>
  </div>
</template>

<script>
import IssueForm from "../components/IssueForm";
export default {
  setup() {},
  data() {
    return {
      projectId: 0,
      project: null,
      dialog: false,
    };
  },
  props: {
    data: Object,
  },
  created() {
    this.projectId = this.$route.query.projectId;
  },
  components: {
    IssueForm,
  },
  methods: {
    toggleDialog() {
      this.dialog = !this.dialog;
    },
  },
  computed: {
    getIssues() {
      return this.$store.getters.getCurrentUser.project.find((p) => p.project_id == this.projectId).issue;
    },
  },
};
</script>

<style scoped>
</style>