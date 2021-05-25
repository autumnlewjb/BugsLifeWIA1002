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
            <v-card v-for="issue in issues" :key="issue.id" class="ma-5">
              <v-card-title>
                {{ issue.title }}
                <v-icon v-for="n in issue.priority" :key="n" color="red"
                  >mdi-exclamation</v-icon
                >
              </v-card-title>
              <v-card-text>
                <v-chip-group v-if="issue.tag">
                  <v-chip v-for="tag in issue.tag" :key="tag" class="mx-1">{{ tag }}</v-chip>
                </v-chip-group>
              </v-card-text>
              <v-card-text>
                {{
                  issue.descriptionText == null || issue.descriptionText == ''
                    ? "(Description not specified)"
                    : issue.descriptionText
                }}</v-card-text
              >
              <v-card-text>
                Status: {{ issue.status }}<br />
                Last updated on
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
      issues: null
    };
  },
  props: {
    data: Object,
  },
  created() {
    this.projectId = this.$route.query.projectId;
    this.fetchIssues();
  },
  components: {
    IssueForm,
  },
  methods: {
    toggleDialog() {
      this.dialog = !this.dialog;
      this.fetchIssues();
    },
    fetchIssues() {
      fetch(`/api/${this.projectId}`)
    .then((res) => {
      if (res.status == 200) {
        return res.json();
      } else {
        return null;
      }
    })
    .then((data) => {
      this.issues = data;
    })
    .catch((e) => console.log(e));
    }
  },
  computed: {
    getIssues() {
      return this.issues;
    },
  },
};
</script>

<style scoped>
</style>