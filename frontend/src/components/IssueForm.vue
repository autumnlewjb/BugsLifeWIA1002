<template>
  <v-card>
    <v-card-title>
      <span class="headline">{{ formTitle }}</span>
    </v-card-title>
    <v-card-text>
      <v-container>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-text-field
                label="Issue Title"
                v-model="title"
                :rules="[rules.required,rules.counter]"
                :counter=this.length
            ></v-text-field>
          </v-row>
          <v-row>
            <!-- <v-textarea
              solo
              label="Description"
              v-model="descriptionText"
              required
            ></v-textarea> -->
            <div style="width: 100%" class="my-5">
              <Editor v-model="descriptionText" placeholder="Write issue description..."/>
            </div>
          </v-row>
          <v-row>
            <v-combobox :items="users" v-model="assignee" label="Assignee"></v-combobox>
          </v-row>
          <v-row>
            <v-combobox label="Tag (use comma to separate multiple tags)" v-model="tag" :items="tagOptions" chips
                        multiple></v-combobox>
          </v-row>
          <br/>
          <p class="body-1">Priority: <br/></p>
          <v-row>
            <v-rating
                v-model="priority"
                full-icon="mdi-exclamation"
                empty-icon="mdi-exclamation"
                color="red"
                length="10"
            >
            </v-rating>
          </v-row>
        </v-form>
      </v-container>
    </v-card-text>
    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn color="blue darken-1" text @click="closeDialog" :disabled="loading">
        Close
      </v-btn>
      <v-btn color="blue darken-1" text @click="validateForm(action)" :disabled="loading" :loading="loading"> Save</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
import Editor from "../components/Editor"

export default {
  name: "IssueForm",
  setup() {
  },
  components: {
    Editor
  },
  data() {
    return {
      title: "",
      priority: 0,
      tag: [],
      descriptionText: "",
      assignee: "",
      action: 'add',
      tagOptions: ['Frontend', 'Backend', 'Suggestion', 'First Bug', 'Enhancement'],
      formTitle: "Add New Issue",
      users: ["lewjb", "jkjsdklfd", "ufifj", "sdkfjksladf", "sadjflsd", "osdfiopsd"],
      loading: false,
      length: 100,
      rules: {
        required: value => !!value || 'This field cannot be empty',
        counter: value => value.length <= this.length || 'Max '+this.length+' characters',
      }
    };
  },
  created() {
    if (this.issue) {
      this.title = this.issue.title;
      this.priority = this.issue.priority;
      this.tag = this.issue.tag;
      this.descriptionText = this.issue.descriptionText;
      this.assignee = this.issue.assignee;
      this.action = 'edit';
      this.formTitle = "Edit Issue";
    }
    fetch(`/api/user/allUsername`)
        .then(res => {
          if (res.status == 200) {
            return res.json()
          } else {
            return [];
          }
        })
        .then(data => this.users = data)
        .catch(e => console.log(e));
  },
  methods: {
    async onSubmit(action) {
      this.loading = true;
      console.log(action);
      if (action == "add") {
        await fetch(`/api/${this.projectId}`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            title: this.title,
            descriptionText: this.descriptionText,
            priority: this.priority,
            status: "Open",
            tag: this.tag,
            createdBy: this.$store.getters.getCurrentUser.username,
            assignee: this.assignee,
            timestamp: Date.now()
          }),
        })
            .then((res) => {
              if (res.status == 200) {
                console.log("added new issue");
                this.closeDialog();
                this.$emit("show-snackbar", "Added new issue")
                this.$store.dispatch('fetchCurrentUser')
              } else {
                this.$emit("show-snackbar", "Issue Not Added !!")
              }
            })
            .catch((e) => console.log(e))
            .finally(() => this.loading = false);
      } else if (action == "edit") {
        console.log(this.title)
        await fetch(`/api/${this.projectId}/${this.issue.issueId}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            title: this.title,
            descriptionText: this.descriptionText,
            priority: this.priority,
            status: this.issue.status,
            tag: this.tag,
            createdBy: this.$store.getters.getCurrentUser.username,
            assignee: this.assignee,
            timestamp: Date.now()
          }),
        })
            .then((res) => {
              if (res.status == 200) {
                this.$store.dispatch('fetchCurrentUser');
                this.closeDialog()
                this.$emit("show-snackbar", "Update successful")
                console.log("update successful");
              } else if (res.status == 403) {
                this.$emit('toggleForbiddenDialog');
              } else {
                console.log("update unsuccessful");
                this.$emit("show-snackbar", "Update unsuccessful")
              }
            })
            .catch((e) => console.log(e))
            .finally(() => this.loading = false)
      }
    },
    clearForm() {
      this.title = "";
      this.priority = 0;
      this.tag = [];
      this.descriptionText = "";
      this.assignee = ""
    },
    validateForm(action) {
      const valid = this.$refs.form.validate();
      if (valid) {
        this.onSubmit(action)
      }
    },
    closeDialog() {
      this.clearForm();
      this.$emit('toggleDialog');
    }
  },
  props: {
    data: Object,
    projectId: {},
    issue: Object
  }
};
</script>