<template>
  <v-container v-if="project">
    <v-layout row>
      <v-flex xs8 md8>
        <v-container>
          <h1>
            {{ getProject.name }}
            <v-btn class="mx-0" plain @click="editProject">Edit</v-btn>
            <v-btn
              class="mx-0"
              plain
              color="red"
              @click="toggleDeleteDialog(false)"
              >Delete</v-btn
            >
          </h1>
        </v-container>
      </v-flex>
    </v-layout>
    <v-layout row>
      <v-flex xs8 md8>
        <v-container>
          <p>Description: {{ getProject.description }}</p>
          <p>
            Date created:
            {{ getProject.date == null ? "Not specified" : getProject.date }}
          </p>
        </v-container>
      </v-flex>
    </v-layout>
    <v-tabs>
      <v-tab>Charts</v-tab>
      <v-tab>Issues</v-tab>
      <v-tab-item>
        <v-container>
          <v-container class="d-flex justify-end">
           <v-btn :href="`/api/${projectId}/charts`" target="blank" color="primary" icon><v-icon>mdi-open-in-new</v-icon></v-btn>
          </v-container>
          <Charts :projectId="projectId"/>
        </v-container>
      </v-tab-item>
      <v-tab-item>
        <v-container>
          <Issues :data="data"></Issues>
        </v-container>
      </v-tab-item>
    </v-tabs>

    <v-dialog v-model="dialog" persistent max-width="600px">
      <ProjectForm
        :project="getProject"
        @toggleDialog="toggleDialog"
        @toggleForbiddenDialog="toggleForbiddenDialog"
        :data="data"
      />
    </v-dialog>
    <ConfirmDelete
      @toggleDeleteDialog="toggleDeleteDialog"
      :showDialog="confirmDeleteDialog"
    />
    <Forbidden :dialog="forbiddenDialog" @closeDialog="closeForbiddenDialog"/>
  </v-container>
</template>

<script>
import Issues from "../views/Issues";
import ProjectForm from "../components/ProjectForm";
import ConfirmDelete from "../components/ConfirmDelete";
import Forbidden from "../components/Forbidden";
import Charts from "../components/Charts";

export default {
  data() {
    return {
      projectId: 0,
      project: null,
      showIssues: false,
      dialog: false,
      confirmDeleteDialog: false,
      forbiddenDialog: false
    };
  },
  setup() {},
  props: {
    data: Object,
  },
  created() {
    this.projectId = this.$route.query.projectId;
    this.fetchProject();
  },
  methods: {
    toggleIssues() {
      if (this.$vuetify.breakpoint.mdAndUp) {
        this.showIssues = !this.showIssues;
      } else {
        this.$router.push({
          name: "Issues",
          query: { projectId: this.projectId },
        });
      }
    },
    editProject() {
      this.dialog = true;
    },
    deleteProject() {
      fetch(`/api/${this.projectId}`, {
        method: "DELETE",
      })
        .then((res) => {
          if (res.status == 200) {
            console.log("delete successful");
            this.$store.dispatch("fetchCurrentUser");
            this.$router.push({ name: "Projects" });
          } else if (res.status == 403) {
            this.forbiddenDialog = true;
          }else {
            console.log("delete unsuccessful");
          }
        })
        .catch((e) => console.log(e));
    },
    toggleDialog() {
      this.fetchProject();
      this.dialog = !this.dialog;
    },
    toggleDeleteDialog(userResponse) {
      this.confirmDeleteDialog = !this.confirmDeleteDialog;
      if (userResponse) {
        this.deleteProject();
        this.fetchProject();
      }
    },
    fetchProject() {
      fetch(`/api/project/${this.projectId}`)
        .then((res) => {
          if (res.status == 200) {
            return res.json();
          } else {
            return null;
          }
        })
        .then((data) => {
          this.project = data;
        })
        .catch((e) => console.log(e));
    },
    closeForbiddenDialog() {
      this.forbiddenDialog = false;
    },
    toggleForbiddenDialog() {
      console.log("toggle")
      this.forbiddenDialog = true;
    }
  },
  components: {
    Issues,
    ProjectForm,
    ConfirmDelete,
    Forbidden,
    Charts
  },
  computed: {
    getProject() {
      return this.project;
    },
  },
};
</script>

<style scoped>
</style>