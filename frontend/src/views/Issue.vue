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
            <v-combobox :items="items" v-model="select"></v-combobox>
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
          :projectId="projectId"
          @updateComment="updateComment"
        />
        <v-card class="pa-5 ma-5" outlined>
          <v-textarea solo :no-resize="true" v-model="text"></v-textarea>
          <v-btn text color="teal" class="" @click="postComment"
            >Post Comment</v-btn
          >
        </v-card>
        <p>
            <a @click="handleUndoRedo('undo')" class="mx-5 text-decoration-underline">Undo last posted comment</a>
            <a @click="handleUndoRedo('redo')" class="text-decoration-underline">Redo</a>
        </p>
      </v-flex>
    </v-layout>
    <v-dialog v-model="dialog" persistent max-width="600px">
      <IssueForm
        @toggleDialog="toggleEditDialog"
        @toggleForbiddenDialog="toggleForbiddenDialog"
        :data="data"
        :issue="getIssue"
        :projectId="projectId"
      />
    </v-dialog>
    <ConfirmDelete
      @toggleDeleteDialog="toggleDeleteDialog"
      :showDialog="confirmDeleteDialog"
    />
    <Forbidden :dialog="forbiddenDialog" @closeDialog="closeForbiddenDialog"/>
    <v-dialog v-model="undoRedoFailed" width="500">
      <v-card>
        <v-card-title class="headline grey lighten-2">Undo / Redo Failed</v-card-title>
        <v-card-text class="my-2">Seems you reach the end of the undo redo stack!</v-card-text>
        <v-card-actions class="d-flex justify-end">
          <v-btn @click="undoRedoFailed = false" text>
            Dismiss
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import Comment from "../components/Comment";
import IssueForm from "../components/IssueForm";
import ConfirmDelete from "../components/ConfirmDelete";
import Forbidden from "../components/Forbidden"

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
      forbiddenDialog: false,
      items: [],
      select: "",
      issueLoaded: false,
      undoRedoFailed: false
    };
  },
  created() {
    this.projectId = this.$route.query.projectId;
    this.issueId = this.$route.query.issueId;
    this.fetchIssue();
  },
  watch: {
    select(val) {
      if (!this.issueLoaded) {
        this.issueLoaded = true;
        return;
      }
      this.issue.status = val;
      fetch(`/api/${this.projectId}/${this.issueId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(this.issue)
      })
      .then((res) => {
        this.fetchIssue();
        if (res.status == 403) {
          this.forbiddenDialog = true;
        } else if (res.status != 200) {
          alert('Issue not updated!');
        }
      })
    }
  },
  props: {
    data: Object,
  },
  components: {
    Comment,
    IssueForm,
    ConfirmDelete,
    Forbidden
  },
  methods: {
    async postComment() {
      await fetch(`/api/${this.projectId}/${this.issueId}`, {
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
      fetch(`/api/${this.projectId}/${this.issueId}`, {
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
          } else if (res.status == 403) {
            this.forbiddenDialog = true;
          }else {
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
        .then((data) => {
          this.issue = data;
          this.select = data.status;
          switch (data.status) {
            case 'Open':
              this.items = ['Resolved', 'Closed', 'In Progress'];
              break;
            case 'Resolved':
              this.items = ['Closed', 'Reopened'];
              break;
            case 'Closed':
              this.items = ['Reopened'];
              break;
            case 'In Progress':
              this.items = ['Closed', 'Resolved'];
              break;
            case 'Reopened':
              this.items = ['Resolved'];
              break;
          }
        })
        .catch((e) => console.log(e));
    },
    updateComment() {
      console.log("comment updated")
      this.fetchIssue();
    },
    closeForbiddenDialog() {
      this.forbiddenDialog = false;
    },
    toggleForbiddenDialog() {
      this.forbiddenDialog = true;
    },
    handleUndoRedo(action, check = true) {
      if (action == 'undo') {
        fetch(`/api/comment/undo`)
        .then((res) => {
          if (res.status != 200) {  
            console.log(res.status);
            return null;
          } else {
            return res.json();
          }
        })
        .then((data) => {
          console.log(data);
          if (data) {
            if (check && data.issue_id != this.issueId) {
              this.undoRedoFailed = true;
              this.handleUndoRedo('redo', false);
            } else {
              this.updateComment();
            }
          } else {
            this.undoRedoFailed = true;
          }
        })
        .catch(e => console.log(e));
      } else {
        fetch(`/api/comment/redo`)
        .then((res) => {
          console.log(res.status);
          if (res.status != 200) {
            return null;
          } else {
            console.log(res);
            return res.json();
          }
        })
        .then((data) => {
          console.log(data);
          if (data) {
            if (check && data.issue_id != this.issueId) {
              this.undoRedoFailed = true;
              this.handleUndoRedo('undo', false);
            } else {
              this.updateComment();
            }
          } else {
            this.undoRedoFailed = true;
          }
        })
        .catch(() => this.undoRedoFailed = true);
      }
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