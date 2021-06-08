<template>
  <v-card>
    <v-card-title>
      <span class="headline">{{ title }}</span>
    </v-card-title>
    <v-card-text>
      <v-container>
        <v-row>
          <v-text-field label="Project Name" v-model="name" required></v-text-field>
        </v-row>
        <v-row>
          <!-- <v-textarea solo
            label="Project Description"
            v-model="description"
          ></v-textarea> -->
          <div style="width: 100%">
            <TipTap v-model="description" placeholder="Write project description"/>
          </div>
        </v-row>
      </v-container>
    </v-card-text>
    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn color="blue darken-1" text @click="onSubmit('close')" :disabled="loading"> Close</v-btn>
      <v-btn color="blue darken-1" text @click="onSubmit(action)" :loading="loading" :disabled="loading"> Save</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
import TipTap from '../components/TipTap';

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
      loading: false
    };
  },
  components: {
    TipTap
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
        await fetch(`/api/`, {
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
            this.$emit("show-snackbar", "Added new project")
            this.$store.dispatch('fetchCurrentUser')
          } else {
            this.$emit("show-snackbar", "Project Not Added !!")
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
            this.$store.dispatch('fetchCurrentUser')
          } else if (res.status == 403) {
            this.$emit('toggleForbiddenDialog');
          } else {
            this.$emit("show-snackbar", "Project Not Edited !!")
          }
        }).catch((e) => console.log(e))
            .finally(() => this.loading = false)
      }
      this.$emit('toggleDialog');
    }
  },
  props: {
    data: Object,
    project: Object
  },
};
</script>