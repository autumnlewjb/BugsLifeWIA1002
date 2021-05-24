<template>
  <v-card>
    <v-card-title>
      <span class="headline">Add New Issue</span>
    </v-card-title>
    <v-card-text>
      <v-container>
        <v-row>
          <v-text-field
            label="Issue Title"
            v-model="title"
            required
          ></v-text-field>
        </v-row>
        <v-row>
          <v-textarea
            solo
            label="Description"
            v-model="descriptionText"
            required
          ></v-textarea>
        </v-row>
        <v-row>
          <v-text-field label="Tag (use comma to separate multiple tags)" v-model="tag"></v-text-field>
        </v-row>
        <br />
        <p class="body-1">Priority: <br /></p>
        <v-row>
          <v-rating
            v-model="priority"
            full-icon="mdi-exclamation"
            empty-icon="mdi-exclamation"
            color="red"
          >
          </v-rating>
        </v-row>
      </v-container>
    </v-card-text>
    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn color="blue darken-1" text @click="onSubmit('close')">
        Close
      </v-btn>
      <v-btn color="blue darken-1" text @click="onSubmit(action)"> Save </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
export default {
  name: "ProjectForm",
  setup() {},
  data() {
    return {
      title: "",
      priority: 0,
      tag: "",
      descriptionText: "",
      assignee: "",
      action: 'add'
    };
  },
  created() {
    if (this.issue) {
      this.title = this.issue.title;
      this.priority = this.issue.priority;
      this.tag = this.issue.tag.join();
      this.descriptionText = this.issue.descriptionText;
      this.assignee = this.issue.assignee;
      this.action = 'edit';
    }
  },
  methods: {
    clearForm() {
      this.title = "";
      this.priority = 0;
      this.tag = "";
      this.descriptionText = "";
      this.assignee = ""
    },
    async onSubmit(action) {
      if (action == "add") {
        await fetch(`/api/${this.projectId}/issue/create`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            title: this.title,
            descriptionText: this.descriptionText,
            priority: this.priority,
            status: "In progress",
            tag: this.tag.split(",").map((val) => val.trim()),
            createdBy: this.$store.getters.getCurrentUser.username,
            assignee: "",
            timestamp: Date.now()
          }),
        })
          .then((res) => {
            if (res.status == 200) {
              console.log("added new issue");
              this.$store.dispatch('fetchCurrentUser')
            } else {
              alert("Issue Not Added !!");
            }
          })
          .catch((e) => console.log(e));
      } else if (action == "edit") {
        console.log(this.title)
        await fetch(`/api/${this.projectId}/${this.issue.issue_id}/updateIssue`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            title: this.title,
            descriptionText: this.descriptionText,
            priority: this.priority,
            status: "In progress",
            tag: this.tag.split(",").map((val) => val.trim()),
            createdBy: this.$store.getters.getCurrentUser.username,
            assignee: "",
            timestamp: Date.now()
          }),
        })
        .then((res) => {
          if (res.status == 200) {
            this.$store.dispatch('fetchCurrentUser');
            console.log("update successful");
          } else {
            console.log("update unsuccessful");
          }
        })
      }
      this.$emit("toggleDialog");
    },
  },
  props: {
    data: Object,
    projectId: String,
    issue: Object
  }
};
</script>