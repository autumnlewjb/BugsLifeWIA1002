<template>
  <v-container>
    <v-layout row justify-space-around class="ma-5">
      <v-flex xs12 md7>
        <v-container>
          <h1>{{ issue.title }}</h1>
        </v-container>
        <v-container>
          <v-card outlined height="80%">
            <v-card-text height="100%">
              <span class="font-weight-bold">Description:</span> <br />
              {{
                issue.descriptionText == null
                  ? "Not specified"
                  : issue.descriptionText
              }}
            </v-card-text>
          </v-card>
        </v-container>
      </v-flex>
      <v-flex xs12 md3>
        <v-container class="text--body-2 font-weight-light">
          <p>
            Priority: <br />
            <v-icon v-for="n in issue.priority" :key="n" color="red"
              >mdi-exclamation</v-icon
            >
          </p>
          <p>
            Status: <br />
            {{ issue.status }}
          </p>
          <p>
            Tag: <br />
            <v-chip-group>
              <v-chip>{{ issue.tag }}</v-chip>
            </v-chip-group>
          </p>
          <p>
            Created by: <br />
            {{ issue.createdBy == null ? "anonymous" : issue.createdBy }}
          </p>
          <p>
            Assignee: <br />
            {{ issue.assignee == null ? "Nobody for now" : issue.assignee }}
          </p>
          <p>
            Timestamp: <br />
            {{ issue.timestamp == null ? "Not Specified" : issue.timestamp }}
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
        />
        <v-card class="pa-5 ma-5" outlined>
            <v-textarea solo :no-resize="true" v-model="text"></v-textarea>
            <v-btn text color="teal" class="" @click="postComment">Post Comment</v-btn>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import Comment from "../components/Comment";
export default {
  setup() {},
  data() {
    return {
      projectId: 0,
      issueId: 0,
      project: null,
      issue: null,
      text: ''
    };
  },
  created() {
    this.projectId = this.$route.query.projectId;
    this.issueId = this.$route.query.issueId;
    this.project = this.$store.getters.getCurrentUser.project.find(
      (project) => project.project_id == this.projectId
    );
    this.issue = this.project.issue.find(
      (issue) => issue.issue_id == this.issueId
    );
    // this.$emit("addToBreadcrumb", {
    //   text: this.issue.title,
    //   disabled: false,
    //   href: window.location.href,
    // });
  },
  props: {
    data: Object,
  },
  components: {
    Comment,
  },
  // beforeRouteLeave(to, from, next) {
  //   this.$emit("removeFromBreadcrumb");
  //   next();
  // },
  methods: {
    postComment() {
      fetch(`${process.env.VUE_APP_BACKEND_URL}/api/${this.issueId}/comment/create`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          text: this.text,
          timestamp: Date.now()
        })
      })
      .then((res) => {
        if (res.status == 200) {
          console.log('comment added')
          // this.$emit('updateUserData')
          this.$store.dispatch('fetchCurrentUser');
        }
      }).catch((e) => console.log(e))
    }
  },
  computed: {
    getComments() {
      return this.$store.getters.getCurrentUser.project.find(
      (project) => project.project_id == this.projectId
    ).issue.find(
      (issue) => issue.issue_id == this.issueId
    ).comment;
    }
  }
};
</script>

<style scoped>
</style>