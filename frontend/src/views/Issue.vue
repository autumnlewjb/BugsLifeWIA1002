<template>
  <v-container v-if="getIssue">
    <v-layout row justify-space-around class="ma-5">
      <v-flex xs12 md7>
        <v-container>
          <h1>
            {{ getIssue.title }}
            <v-btn @click="editIssue" plain>Edit</v-btn>
            <v-btn @click="toggleDeleteDialog(false)" plain color="red"
              >Delete</v-btn
            >
          </h1>
        </v-container>
        <v-container>
          <v-card outlined height="80%">
            <v-card-text height="100%">
              <span class="font-weight-bold">Description:</span> <br />
              {{
                getIssue.descriptionText == null
                  ? "Not specified"
                  : getIssue.descriptionText
              }}
            </v-card-text>
          </v-card>
        </v-container>
      </v-flex>
      <v-flex xs12 md3>
        <v-container class="text--body-2 font-weight-light">
          <p>
            Priority: <br />
            <v-icon v-for="n in getIssue.priority" :key="n" color="red"
              >mdi-exclamation</v-icon
            >
          </p>
          <p>
            Status: <br />
            {{ getIssue.status }}
          </p>
          <p>
            Tag: <br />
            <!-- <v-chip-group> -->
            <v-chip v-for="tag in getIssue.tag" :key="tag" class="ma-1">{{
              tag
            }}</v-chip>
            <!-- </v-chip-group> -->
          </p>
          <p>
            Created by: <br />
            {{ getIssue.createdBy == null ? "anonymous" : getIssue.createdBy }}
          </p>
          <p>
            Assignee: <br />
            {{ getIssue.assignee == null ? "Nobody for now" : getIssue.assignee }}
          </p>
          <p>
            Last updated: <br />
            {{ getIssue.timestamp == null ? "Not Specified" : getIssue.timestamp }}
          </p>
        </v-container>
      </v-flex>
    </v-layout>
    <v-layout row justify-space-around>
      <v-flex md10>
        <Comment
          v-for="comment in getComments"
          :key="comment.id"
          :comment="comment"
          :issueId="issueId"
          @updateComment="updateComment"
        />
        <v-card class="pa-5 ma-5" outlined>
          <v-textarea solo :no-resize="true" v-model="text"></v-textarea>
          <v-btn text color="teal" class="" @click="postComment"
            >Post Comment</v-btn
          >
        </v-card>
      </v-flex>
    </v-layout>
    <v-dialog v-model="dialog" persistent max-width="600px">
      <IssueForm
        @toggleDialog="toggleEditDialog"
        :data="data"
        :issue="getIssue"
        :projectId="projectId"
      />
    </v-dialog>
    <ConfirmDelete
      @toggleDeleteDialog="toggleDeleteDialog"
      :showDialog="confirmDeleteDialog"
    />
  </v-container>
</template>

<script>
import Comment from "../components/Comment";
import IssueForm from "../components/IssueForm";
import ConfirmDelete from "../components/ConfirmDelete";

export default {
  setup() {},
  data() {
    return {
      projectId: 0,
      issueId: 0,
      project: null,
      issue: null,
      text: "",
      dialog: false,
      confirmDeleteDialog: false,
    };
  },
  created() {
    this.projectId = this.$route.query.projectId;
    this.issueId = this.$route.query.issueId;
    this.fetchIssue();
  },
  props: {
    data: Object,
  },
  components: {
    Comment,
    IssueForm,
    ConfirmDelete,
  },
  methods: {
    async postComment() {
      await fetch(`/api/${this.issueId}/comment/create`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          text: this.text,
          timestamp: Date.now(),
        }),
      })
        .then((res) => {
          if (res.status == 200) {
            console.log("comment added");
            this.text = "";
            this.$store.dispatch("fetchCurrentUser");
          }
        })
        .catch((e) => console.log(e));
      this.fetchIssue();
    },
    toggleEditDialog() {
      this.dialog = !this.dialog;
      this.fetchIssue();
    },
    toggleDeleteDialog(userResponse) {
      this.confirmDeleteDialog = !this.confirmDeleteDialog;
      if (userResponse) {
        this.deleteIssue();
        this.fetchIssue();
      }
    },
    editIssue() {
      this.dialog = true;
    },
    deleteIssue() {
      fetch(`/api/project/${this.projectId}/delete/issue/${this.issueId}`, {
        method: "DELETE",
      })
        .then((res) => {
          if (res.status == 200) {
            console.log("delete successful");
            this.$store.dispatch("fetchCurrentUser");
            this.$router.push({
              name: "Project",
              query: { projectId: this.projectId },
            });
          } else {
            console.log("delete unsuccessful");
          }
        })
        .catch((e) => console.log(e));
    },
    fetchIssue() {
      fetch(`/api/issue/${this.issueId}`)
        .then((res) => {
          if (res.status == 200) {
            return res.json();
          } else {
            return null;
          }
        })
        .then((data) => (this.issue = data))
        .catch((e) => console.log(e));
    },
    updateComment() {
      console.log("comment updated")
      this.fetchIssue();
    }
  },
  computed: {
    getComments() {
      return this.issue.comment;
    },
    getIssue() {
      return this.issue;
    },
  },
};
</script>

<style scoped>
</style>