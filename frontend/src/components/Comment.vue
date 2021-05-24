<template>
  <div class="comment">
    <v-card class="ma-2" outlined>
      <v-card-text class="text--body-1 black--text blue-grey lighten-4"
        ><strong
          >Commented on
          {{
            comment.timestamp != null ? " " + comment.timestamp : "unknown time"
          }}</strong
        ></v-card-text
      >
      <v-card-text class="text--body-1 black--text">{{
        comment.text
      }}</v-card-text>
      <v-card-text>
        <v-layout row>
          <v-flex xs6 md8>
            <v-chip @click="addEmoji('angry')" class="ma-1">
              &#128520; {{ angryCount }}
            </v-chip>
            <v-chip @click="addEmoji('happy')" class="ma-1">
              &#128516; {{ happyCount }}
            </v-chip>
            <v-chip @click="addEmoji('thumbsup')" class="ma-1">
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
        <v-textarea solo :no-resize="true" v-model="text"></v-textarea>
        <v-card-actions>
          <v-btn text color="teal" class="" @click="editComment"
            >Edit Comment</v-btn
          >
          <v-btn text color="red" @click="toggleEditDialog">Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <ConfirmDelete @toggleDeleteDialog="toggleDeleteDialog" :showDialog="confirmDeleteDialog" />
  </div>
</template>

<script>
import ConfirmDelete from "../components/ConfirmDelete"
export default {
  setup() {},
  components: {
    ConfirmDelete
  },
  data() {
    return {
      angry: this.comment.react.find((r) => r.reaction === "angry"),
      happy: this.comment.react.find((r) => r.reaction === "happy"),
      thumbsup: this.comment.react.find((r) => r.reaction === "thumbsup"),
      angryCount: 0,
      happyCount: 0,
      thumbsupCount: 0,
      dialog: false,
      text: this.comment.text,
      confirmDeleteDialog: false
    };
  },
  created() {
    this.angryCount = this.angry ? this.angry.count : 0;
    this.happyCount = this.happy ? this.happy.count : 0;
    this.thumbsupCount = this.thumbsup ? this.thumbsup.count : 0;
  },
  props: {
    comment: Object,
    issueId: undefined,
  },
  methods: {
    async addEmoji(str) {
      if (str == "happy") {
        var url = `/api/${this.comment.comment_id}/reaction/create`;
        var met = "POST";
        var data = {
          reaction: "happy",
          count: 1,
        };
        if (this.happy) {
          url = `/api/${this.comment.comment_id}/${this.happy.react_id}/updateReaction`;
          met = "PUT";
          data = {
            reaction: "happy",
            count: this.happyCount + 1,
          };
        }
        await fetch(url, {
          method: met,
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(data),
        })
          .then((res) => {
            if (res.status == 200) {
              this.$store.dispatch("fetchCurrentUser");
              console.log("successful");
              this.happyCount ++;
            } else {
              console.log("failed");
            }
          })
          .catch((e) => console.log(e));
      } else if (str == "angry") {
        url = `/api/${this.comment.comment_id}/reaction/create`;
        met = "POST";
        data = {
          reaction: "angry",
          count: 1,
        };
        if (this.angry) {
          url = `/api/${this.comment.comment_id}/${this.angry.react_id}/updateReaction`;
          met = "PUT";
          data = {
            reaction: "angry",
            count: this.angryCount + 1,
          };
        }
        await fetch(url, {
          method: met,
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(data),
        })
          .then((res) => {
            if (res.status == 200) {
              this.$store.dispatch("fetchCurrentUser");
              console.log("successful");
              this.angryCount ++;
            } else {
              console.log("failed");
            }
          })
          .catch((e) => console.log(e));
      } else {
        url = `/api/${this.comment.comment_id}/reaction/create`;
        met = "POST";
        data = {
          reaction: "thumbsup",
          count: 1,
        };
        if (this.thumbsup) {
          url = `/api/${this.comment.comment_id}/${this.thumbsup.react_id}/updateReaction`;
          met = "PUT";
          data = {
            reaction: "thumbsup",
            count: this.thumbsupCount + 1,
          };
        }
        await fetch(url, {
          method: met,
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(data),
        })
          .then((res) => {
            if (res.status == 200) {
              this.$store.dispatch("fetchCurrentUser");
              console.log("successful");
              this.thumbsupCount ++;
            } else {
              console.log("failed");
            }
          })
          .catch((e) => console.log(e));
      }
      this.$emit('updateComment')
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
        `/api/issue/${this.issueId}/delete/comment/${this.comment.comment_id}`,
        {
          method: "DELETE",
        }
      ).then((res) => {
        if (res.status == 200) {
          this.$store.dispatch("fetchCurrentUser");
        } else {
          console.log("delete unsuccessful");
        }
      });
      this.$emit('updateComment');
    },
    async editComment() {
      await fetch(`/api/${this.issueId}/${this.comment.comment_id}/updateComment`, {
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
          } else {
            console.log("update failed");
          }
        })
        .catch((e) => console.log(e));
      this.$emit('updateComment');
    },
  },
  computed: {
    getHappy() {
      return this.happy.count;
    },
  },
};
</script>

<style scoped>
</style>