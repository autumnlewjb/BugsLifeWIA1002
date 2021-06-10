<template>
  <v-container v-if="getIssue">
    <v-layout row justify-space-around class="ma-5">
      <v-flex xs12 md7>
        <v-container>
          <h1>
            {{ getIssue.title }}
          </h1>
          <v-btn @click="editIssue" plain>Edit</v-btn>
          <v-btn @click="toggleDeleteDialog(false)" plain color="red"
          >Delete
          </v-btn
          >
          <v-menu offset-y>
            <template v-slot:activator="{ on, attrs }">
              <v-btn icon v-on="on" v-bind="attrs"
              >
                <v-icon>mdi-history</v-icon>
              </v-btn
              >
            </template>
            <v-list>
              <v-list-item @click="handleUndoRedo('undo')">Undo</v-list-item>
              <v-list-item @click="handleUndoRedo('redo')">Redo</v-list-item>
              <v-list-item @click="showChangelog = true"
              >Changelog
              </v-list-item
              >
            </v-list>
          </v-menu>
        </v-container>
        <v-container>
          <v-card outlined class="pa-5">
            <span
                class="font-weight-normal"
                v-html="
                getIssue.descriptionText == null
                  ? 'Not specified'
                  : getIssue.descriptionText
              "
            >Description</span
            >
            <br/>
          </v-card>
        </v-container>
        <v-container>
          <Attachment :issueId="issueId"/>
        </v-container>
      </v-flex>
      <v-flex xs12 md3>
        <v-container class="text--body-2 font-weight-light">
          <p>
            <span class="font-weight-bold">Priority</span> <br/>
            <v-icon v-for="n in getIssue.priority" :key="n" color="red"
            >mdi-exclamation
            </v-icon
            >
          </p>
          <p>
            <span class="font-weight-bold">Status</span> <br/>
            <v-combobox s :items="items" v-model="select" ref="combobox" :loading="statusLoading"></v-combobox>
          </p>
          <p>
            <span class="font-weight-bold">Tag</span> <br/>
            <!-- <v-chip-group> -->
            <v-chip v-for="tag in getIssue.tag" :key="tag" class="ma-1">{{
                tag
              }}
            </v-chip>
            <!-- </v-chip-group> -->
          </p>
          <p>
            <span class="font-weight-bold">Created by</span> <br/>
            {{ getIssue.createdBy == null ? "anonymous" : getIssue.createdBy }}
          </p>
          <p>
            <span class="font-weight-bold">Assignee</span> <br/>
            {{
              getIssue.assignee == null ? "Nobody for now" : getIssue.assignee
            }}
          </p>
          <p>
            <span class="font-weight-bold">Last updated</span> <br/>
            {{
              getIssue.modifiedDate != null ? new Date(getIssue.modifiedDate).toLocaleString() :
                  getIssue.timestamp == null ? "Not Specified" : new Date(getIssue.timestamp).toLocaleString()
            }}
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
            @show-snackbar="toggleSnackbar"
        />
        <v-card class="pa-5 ma-5" outlined>
          <!-- <v-textarea solo :no-resize="true" v-model="text"></v-textarea> -->
          <TipTap v-model="text" placeholder="Write a comment..."/>
          <v-btn text color="teal" class="" @click="postComment" :loading="loading" :disabled="loading"
          >Post Comment
          </v-btn
          >
        </v-card>
      </v-flex>
    </v-layout>
    <v-dialog v-model="dialog" persistent max-width="600px">
      <IssueForm
          @toggleDialog="toggleEditDialog"
          @toggleForbiddenDialog="toggleForbiddenDialog"
          @show-snackbar="toggleSnackbar"
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
        <v-card-title class="headline grey lighten-2"
        >Undo / Redo Failed
        </v-card-title
        >
        <v-card-text class="my-2"
        >Seems you reach the end of the undo redo stack!
        </v-card-text
        >
        <v-card-actions class="d-flex justify-end">
          <v-btn @click="undoRedoFailed = false" text> Dismiss</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-dialog v-model="showChangelog" width="1000">
      <Changelog :id="issueId" type="issue" :changes="getChanges"/>
    </v-dialog>
    <Snackbar :snackbar="snackbar" :text="message" @close-snackbar="closeSnackbar"/>
  </v-container>
</template>

<script>
import Comment from "../components/Comment";
import IssueForm from "../components/IssueForm";
import ConfirmDelete from "../components/ConfirmDelete";
import Forbidden from "../components/Forbidden";
import Changelog from "../components/Changelog";
import TipTap from '../components/TipTap.vue';
import Attachment from "../components/Attachment";
import Snackbar from "../components/Snackbar";

export default {
  setup() {
  },
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
      undoRedoFailed: false,
      showChangelog: false,
      history: [],
      snackbar: false,
      message: null,
      loading: false,
      statusLoading: false
    };
  },
  created() {
    this.projectId = parseInt(this.$route.query.projectId);
    this.issueId = parseInt(this.$route.query.issueId);
    this.fetchIssue();
  },
  watch: {
    select(val) {
      if (!this.issueLoaded) {
        this.issueLoaded = true;
        return;
      }
      this.statusLoading = true;
      this.issue.status = val;
      fetch(`/api/${this.projectId}/${this.issueId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(this.issue),
      }).then((res) => {
        this.fetchIssue();
        this.$refs.combobox.blur();
        this.statusLoading=false;
        this.toggleSnackbar("Update successful")
        if (res.status == 403) {
          this.forbiddenDialog = true;
        } else if (res.status != 200) {
          alert("Issue not updated!");
        }
      });
    },
    showChangelog(val) {
      if (val) {
        this.fetchChangelog();
      }
    }
  },
  props: {
    data: Object,
  },
  components: {
    Comment,
    IssueForm,
    ConfirmDelete,
    Forbidden,
    TipTap,
    Changelog,
    Attachment,
    Snackbar
  },
  methods: {
    async postComment() {
      this.loading = true;
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
              this.toggleSnackbar("Comment added")
              this.text = "";
              this.$store.dispatch("fetchCurrentUser");
            }
          })
          .catch((e) => console.log(e))
          .finally(() => this.loading = false);
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
              this.toggleSnackbar("Delete successful")
              setTimeout(() => this.$router.push({
                name: "Project",
                query: {projectId: this.projectId},
              }), 1500);
            } else if (res.status == 403) {
              this.forbiddenDialog = true;
            } else {
              console.log("delete unsuccessful");
              this.toggleSnackbar("Delete unsuccessful")
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
              case "Open":
                this.items = ["Resolved", "Closed", "In Progress"];
                break;
              case "Resolved":
                this.items = ["Closed", "Reopened"];
                break;
              case "Closed":
                this.items = ["Reopened"];
                break;
              case "In Progress":
                this.items = ["Closed", "Resolved", "Open"];
                break;
              case "Reopened":
                this.items = ["Resolved", "In Progress", "Closed"];
                break;
            }
          })
          .catch((e) => console.log(e));
    },
    fetchChangelog() {
      fetch(`/api/issue/${this.issueId}/history`)
          .then((res) => {
            if (res.status == 200) {
              return res.json();
            } else {
              return null;
            }
          })
          .then((data) => {
            if (data) {
              this.history = data;
            }
          });
    },
    updateComment() {
      console.log("comment updated");
      this.fetchIssue();
    },
    closeForbiddenDialog() {
      this.forbiddenDialog = false;
    },
    toggleForbiddenDialog() {
      this.forbiddenDialog = true;
    },
    handleUndoRedo(action, check = true) {
      if (action == "undo") {
        fetch(`/api/${this.projectId}/${this.issueId}/issue/undo`)
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
                  this.handleUndoRedo("redo", false);
                } else {
                  this.fetchIssue();
                }
              } else {
                this.undoRedoFailed = true;
              }
            })
            .catch((e) => console.log(e));
      } else {
        fetch(`/api/${this.projectId}/${this.issueId}/issue/redo`)
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
                  this.handleUndoRedo("undo", false);
                } else {
                  this.fetchIssue();
                }
              } else {
                this.undoRedoFailed = true;
              }
            })
            .catch(() => (this.undoRedoFailed = true));
      }
    },
    toggleSnackbar(text) {
      this.snackbar = true;
      this.message = text;
    },
    closeSnackbar() {
      this.snackbar = false;
      this.message = null;
    },
  },
  computed: {
    getComments() {
      return this.issue.comment;
    },
    getIssue() {
      return this.issue;
    },
    getIssueDescription() {
      return this.getIssue.descriptionText == null
          ? "Not specified"
          : this.getIssue.descriptionText;
    },
    getChanges() {
      if (this.history.length == 0) return [];
      console.log(this.history);
      const changes = [];
      changes.push({
        date: this.history[this.history.length - 1].timestamp,
        modifier: this.history[this.history.length - 1].createdBy,
        statements: [{description: "created this issue", html: false}],
      });
      var prev = this.history[this.history.length - 1];
      const excluded = [
        "comment",
        "modifiedBy",
        "createdBy",
        "timestamp",
        "modifiedDate",
        "commentNum",
      ];
      for (var i = this.history.length - 2; i >= 0; i--) {
        var curr = this.history[i];
        const change = {
          date: curr.modifiedDate,
          modifier: curr.modifiedBy,
          statements: [],
        };
        Object.keys(curr).forEach((key) => {
          if (!excluded.includes(key)) {
            if (key == "tag") {
              console.log(curr[key]);
              for (var index in curr[key]) {
                if (!prev[key].find((element) => element == curr[key][index])) {
                  change.statements.push({
                    description: "changed the tags to " + curr[key].join(),
                    html: false
                  });
                  break;
                }
              }
            } else {
              if (curr[key] != prev[key]) {
                change.statements.push({
                  description: `modified ${key} from ${prev[key]} to ${curr[key]}`,
                  html: key == 'descriptionText'
                });
              }
            }
          }
        });
        if (change.statements.length > 0) changes.push(change);
        prev = curr;
      }
      changes.reverse();
      return changes;
    },
  },
};

</script>

<style scoped>
.v-combobox:focus:not(.v-combobox--change):after {
  background: none !important;
}
</style>