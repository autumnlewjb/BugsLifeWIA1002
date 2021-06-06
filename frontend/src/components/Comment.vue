<template>
  <div class="comment">
    <v-card class="ma-2" outlined>
      <v-card-text class="text--body-1 black--text blue-grey lighten-4"
        >
        <strong
          >{{comment.user}} commented on
          {{
            comment.timestamp != null ? " " + comment.timestamp : "unknown time"
          }}</strong
        >
        <v-menu offset-y>
          <template v-slot:activator="{ on, attrs }">
            <v-btn icon dense v-on="on" v-bind="attrs">
              <v-icon>mdi-history</v-icon>
            </v-btn>
          </template>
          <v-list>
            <v-list-item @click="handleUndoRedo('undo')">Undo</v-list-item>
            <v-list-item @click="handleUndoRedo('redo')">Redo</v-list-item>
            <v-list-item @click="showChangelog = true">Changelog</v-list-item>
          </v-list>
        </v-menu>
        </v-card-text
      >
      <v-card-text class="text--body-1 black--text" v-html="comment.text"></v-card-text>
      <v-card-text>
        <v-layout row>
          <v-flex xs6 md8>
            <v-chip @click="handleEmojiClick('Angry', angryColor == undefined)" class="ma-1" :disabled="!enableAngry" :color="angryColor" :outlined="myReaction && myReaction.reaction == 'Angry'">
              &#128520; {{ angryCount }}
            </v-chip>
            <v-chip @click="handleEmojiClick('Happy', happyColor == undefined)" class="ma-1" :disabled="!enableHappy" :color="happyColor" :outlined="myReaction && myReaction.reaction == 'Happy'">
              &#128516; {{ happyCount }}
            </v-chip>
            <v-chip @click="handleEmojiClick('Thumbsup', thumbsupColor == undefined)" class="ma-1" :disabled="!enableThumbsup" :color="thumbsupColor" :outlined="myReaction && myReaction.reaction == 'Thumbsup'">
              &#128077; {{ thumbsupCount }}
            </v-chip>
          </v-flex>
          <v-flex xs6 md4 style="text-align: end">
            <v-btn class="mx-0" plain @click="toggleEditDialog">Edit</v-btn>
            <v-btn class="mx-0" plain color="red" @click="toggleDeleteDialog(false)"
              >Delete</v-btn
            >
          </v-flex>
        </v-layout>
      </v-card-text>
    </v-card>
    <v-dialog v-model="dialog" class="" persistent width="600">
      <v-card class="pa-2" outlined>
        <!-- <v-textarea solo :no-resize="true" v-model="text"></v-textarea> -->
        <TipTap v-model="text" placeholder="Write a comment..."/>
        <v-card-actions>
          <v-btn text color="teal" class="" @click="editComment"
            >Edit Comment</v-btn
          >
          <v-btn text color="red" @click="toggleEditDialog">Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <ConfirmDelete @toggleDeleteDialog="toggleDeleteDialog" :showDialog="confirmDeleteDialog" />
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
    <v-dialog v-model="showChangelog">
      <Changelog :changes="getChanges"/>
    </v-dialog>
  </div>
</template>

<script>
import ConfirmDelete from "../components/ConfirmDelete"
import Forbidden from "../components/Forbidden"
import TipTap from '../components/TipTap'
import Changelog from './Changelog.vue';
export default {
  setup() {},
  components: {
    ConfirmDelete,
    Forbidden,
    TipTap,
    Changelog
  },
  data() {
    return {
      angry: [],
      happy: [],
      thumbsup: [],
      angryCount: 0,
      happyCount: 0,
      thumbsupCount: 0,
      dialog: false,
      text: this.comment.text,
      confirmDeleteDialog: false,
      commentId: this.comment.comment_id,
      forbiddenDialog: false,
      myReaction: null,
      enableReaction: false,
      happyColor: undefined,
      angryColor: undefined,
      thumbsupColor: undefined,
      commentObj: null,
      enableHappy: false,
      enableAngry: false,
      enableThumbsup: false,
      undoRedoFailed: false,
      showChangelog: false,
      history: []
    };
  },
  created() {
    this.commentObj = this.comment;
  },
  props: {
    comment: Object,
    issueId: undefined,
    projectId: undefined
  },
  watch: {
    comment(val) {
      this.commentObj = val;
    },
    commentObj(val) {
      console.log("change");
      this.angry = this.comment.react.filter((r) => r.reaction.toLowerCase() === "angry");
      this.happy = this.comment.react.filter((r) => r.reaction.toLowerCase() === "happy");
      this.thumbsup = this.comment.react.filter((r) => r.reaction.toLowerCase() === "thumbsup");
      this.angryCount = this.angry.length;
      this.happyCount = this.happy.length;
      this.thumbsupCount = this.thumbsup.length;
      this.myReaction = val.react.find((r) => r.reactionBy == this.$store.getters.getCurrentUser.username);
      if (this.myReaction) {
        this.enableHappy = this.myReaction.reaction == 'Happy';
        this.enableAngry = this.myReaction.reaction == 'Angry';
        this.enableThumbsup = this.myReaction.reaction == 'Thumbsup';
        this.happyColor = !this.enableReaction && this.myReaction.reaction == 'Happy' ? '#8cc7ff' : undefined;
        this.angryColor = !this.enableReaction && this.myReaction.reaction == 'Angry' ? '#8cc7ff' : undefined;
        this.thumbsupColor = !this.enableReaction && this.myReaction.reaction == 'Thumbsup' ? '#8cc7ff' : undefined;
      } else {
        this.enableHappy = true;
        this.enableAngry = true;
        this.enableThumbsup = true;
        this.happyColor = undefined;
        this.thumbsupColor = undefined;
        this.angryColor = undefined;
      }
    },
    showChangelog(val) {
      if (val) {
        fetch(`/api/comment/${this.commentId}/history`)
        .then((res) => {
          if (res.status == 200) {
            return res.json();
          } else {
            return null;
          }
        })
        .then((data) => {
          if (data) {
            console.log(data);
            this.history = data;
          }
        })
        .catch(e => console.log(e));
      }
    }
  },
  methods: {
    handleEmojiClick(str, notReacted) {
      if (notReacted) {
        this.addEmoji(str);
      } else {
        this.removeEmoji();
      }
    },
    async addEmoji(str) {
      var url = `/api/${this.projectId}/${this.issueId}/${this.commentId}`;
      var data = {
        reaction: str,
      };
      await fetch(url, {
        method: 'POST',
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
      })
        .then((res) => {
          if (res.status == 200) {
            this.$store.dispatch("fetchCurrentUser");
            console.log("successful");
          } else {
            console.log("failed");
          }
        })
        .catch((e) => console.log(e));
      this.$emit('updateComment');
    },
    removeEmoji() {
      fetch(`/api/${this.projectId}/${this.issueId}/${this.commentId}/del`, {
        method: 'DELETE'
      })
      .then(() => {
        this.$emit('updateComment');
      })
    },
    toggleEditDialog() {
      this.dialog = !this.dialog;
    },
    toggleDeleteDialog(userResponse) {
      this.confirmDeleteDialog = !this.confirmDeleteDialog;
      if (userResponse) {
        this.deleteComment();
      }
    },
    async deleteComment() {
      await fetch(
        `/api/${this.projectId}/${this.issueId}/${this.commentId}`,
        {
          method: "DELETE",
        }
      ).then((res) => {
        if (res.status == 200) {
          this.$store.dispatch("fetchCurrentUser");
        } else if (res.status == 403) {
          this.forbiddenDialog = true;
        } else {
          console.log("delete unsuccessful");
        }
      });
      this.$emit('updateComment');
    },
    async editComment() {
      await fetch(`/api/${this.projectId}/${this.issueId}/${this.commentId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          text: this.text,
          timestamp: this.comment.timestamp,
        }),
      })
        .then((res) => {
          if (res.status == 200) {
            this.$store.dispatch("fetchCurrentUser");
            console.log("update successful");
            this.toggleEditDialog();
          } else if (res.status == 403) {
            this.forbiddenDialog = true;
          } else {
            console.log("update failed");
          }
        })
        .catch((e) => console.log(e));
      this.$emit('updateComment');
    },
    closeForbiddenDialog() {
      this.forbiddenDialog = false;
    },
    handleUndoRedo(action, check = true) {
      console.log(this.issueId);
      console.log(this.commentId);
      if (action == 'undo') {
        fetch(`/api/${this.issueId}/${this.commentId}/comment/undo`)
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
            if (check && data.comment_id != this.commentId) {
              this.undoRedoFailed = true;
              this.handleUndoRedo('redo', false);
            } else {
              this.$emit('updateComment');
            }
          } else {
            this.undoRedoFailed = true;
          }
        })
        .catch(e => console.log(e));
      } else {
        fetch(`/api/${this.issueId}/${this.commentId}/comment/redo`)
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
            if (check && data.comment_id != this.commentId) {
              this.undoRedoFailed = true;
              this.handleUndoRedo('undo', false);
            } else {
              this.$emit('updateComment');
            }
          } else {
            this.undoRedoFailed = true;
          }
        })
        .catch(() => this.undoRedoFailed = true);
      }
    },
    getText(str) {
      var tmp = document.createElement("DIV");
      tmp.innerHTML = str;
      return tmp.textContent || tmp.innerText || "";
    },
  },
  computed: {
    getHappy() {
      return this.happy.count;
    },
    getChanges() {
      if (this.history.length == 0) return;
      const changes = [];
      var prev = this.history[this.history.length-1];
      changes.push({
        date: this.history[this.history.length-1].timestamp,
        modifier: this.history[this.history.length-1].user,
        statements: [{description: "written the comment", html: false}]
      });
      const excluded = ['user', 'timestamp', 'comment_id', 'react'];
      for (let i=this.history.length-2; i>=0; i--) {
        var curr = this.history[i];
        var change = {
          date: curr.timestamp,
          modifier: curr.user,
          statements: []
        }
        Object.keys(curr).forEach((key) => {
          if (!excluded.includes(key)) {
            if (curr[key] != prev[key]) {
              change.statements.push({description: `modified the ${key} from ${prev[key]} to ${curr[key]}`, html: key == "text"});
            }
          }
        });
        if (change.statements.length > 0) changes.push(change);
      }

      changes.reverse();

      return changes;
    }
  },
};
</script>

<style scoped>
p {
  color: #8cc7ffd3;
}
</style>