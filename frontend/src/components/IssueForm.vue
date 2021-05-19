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
          <v-text-field label="Tag" v-model="tag"></v-text-field>
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
      <v-btn color="blue darken-1" text @click="onSubmit('add')"> Add </v-btn>
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
    };
  },
  methods: {
    clearForm() {
      this.title = "";
      this.priority = 0;
      this.tag = "";
      this.descriptionText = "";
      this.assignee = ""
    },
    onSubmit(action) {
      if (action == "add") {
        fetch(`/api/${this.projectId}/issue/create`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            title: this.title,
            descriptionText: this.descriptionText,
            priority: this.priority,
            status: "In progress",
            tag: this.tag,
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
      }
      this.$emit("toggleDialog");
    },
  },
  props: {
    data: Object,
    projectId: String,
  },
};
</script>