<template>
  <v-card>
    <v-card-title>
      <span class="headline">{{ title }}</span>
    </v-card-title>
    <v-card-text>
      <v-container>
        <v-form ref="form" lazy-validation>
          <v-text-field label="Project Name" v-model="name" :rules="[rules.required,rules.counter]"
                        :counter=this.length></v-text-field>
        </v-form>
        <v-row>
          <!-- <v-textarea solo
            label="Project Description"
            v-model="description"
          ></v-textarea> -->
          <div style="width: 100%">
            <Editor v-model="description" placeholder="Write project description"/>
          </div>
        </v-row>
      </v-container>
    </v-card-text>
    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn color="blue darken-1" text @click="closeDialog" :disabled="loading"> Close</v-btn>
      <v-btn color="blue darken-1" text @click="validateForm(action)" :loading="loading" :disabled="loading"> Save
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
import Editor from '../components/Editor';

export default {
  name: "ProjectForm",
  setup() {
  },
  data() {
    return {
      title: 'Add New Project',
      name: '',
      description: '',
      action: 'add',
      projectId: 0,
      loading: false,
      length: 100,
      rules: {
        required: value => !!value || 'This field cannot be empty',
        counter: value => value.length <= this.length || 'Max '+this.length+' characters',
      }
    }
  },
  components: {
    Editor
  },
  created() {
    if (this.project) {
      this.action = 'edit';
      this.name = this.project.name;
      this.description = this.project.description;
      this.title = 'Edit Project';
      this.projectId = this.project.projectId;
    }
  },
  methods: {
    async onSubmit(action) {
      this.loading = true;
      if (action == 'add') {
        await fetch(`/api/${this.$store.getters.getCurrentUser.user_id}/createProject`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            name: this.name,
            description: this.description,
            date: Date.now()
          })
        }).then((res) => {
          if (res.status == 200) {
            console.log("added new project")
            this.$emit("show-snackbar", "Added new project");
            this.closeDialog();
            this.$store.dispatch('fetchCurrentUser');
          } else {
            this.$emit("show-snackbar", "Project Not Added !!");
          }
        }).catch((e) => console.log(e))
            .finally(() => this.loading = false)
      } else if (action == 'edit') {
        await fetch(`/api/${this.projectId}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            name: this.name,
            description: this.description,
            date: Date.now()
          })
        }).then((res) => {
          if (res.status == 200) {
            console.log("update project")
            this.$emit("show-snackbar", "Updated the project")
            this.closeDialog()
            this.$store.dispatch('fetchCurrentUser')
          } else if (res.status == 403) {
            this.$emit('toggleForbiddenDialog');
          } else {
            this.$emit("show-snackbar", "Project Not Edited !!")
          }
        }).catch((e) => console.log(e))
            .finally(() => this.loading = false)
      }
    },
    clearForm() {
      this.name = "";
      this.description = "";
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
    project: Object
  },
};
</script>