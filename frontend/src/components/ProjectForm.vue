<template>
  <v-card>
    <v-card-title>
      <span class="headline">{{title}}</span>
    </v-card-title>
    <v-card-text>
      <v-container>
        <v-row>
            <v-text-field label="Project Name" v-model="name" required></v-text-field>
        </v-row>
        <v-row>
            <v-textarea solo
              label="Project Description"
              v-model="description"
            ></v-textarea>
        </v-row>
      </v-container>
    </v-card-text>
    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn color="blue darken-1" text @click="onSubmit('close')"> Close </v-btn>
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
        title: 'Add New Project',
        name: '',
        description: '',
        action: 'add'
    };
  },
  created() {
    if (this.projectId) {
      this.action = 'edit';
      const project = this.$store.getters.getCurrentUser.project.find((p) => p.project_id == this.projectId)
      this.name = project.name;
      this.description = project.description;
      this.title = 'Edit Project'
    }
  },
  methods: {
    onSubmit(action) {
        if (action == 'add') {
            fetch(`/api/${this.$store.getters.getCurrentUser.user_id}/project/create`, {
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
                    this.$store.dispatch('fetchCurrentUser')
                } else {
                    alert("Project Not Added !!")
                }
            }).catch((e) => console.log(e))
        } else if (action == 'edit') {
          console.log('edit')
        }
        this.$emit('toggleDialog')
    }
  },
  props: {
    data: Object,
    projectId: String
  }
};
</script>