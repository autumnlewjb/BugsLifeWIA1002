<template>
  <v-container v-if="project">
    <v-container>
      <v-layout row>
        <v-flex xs12 md8>
          <v-container>
            <h1>
              {{ getProject.name }}
            </h1>
            <v-btn class="mx-0" plain @click="editProject">Edit</v-btn>
            <v-btn
                class="mx-0"
                plain
                color="red"
                @click="toggleDeleteDialog(false)"
            >Delete
            </v-btn
            >
            <v-container>
              <p class="subtitle-2">
                {{ getProject.date == null ? "Not specified" : new Date(getProject.date) }}
              </p>
              <v-card outlined class="pa-5 my-10">
                <p v-html="getProjectDescription"></p>
              </v-card>
            </v-container>
          </v-container>
        </v-flex>
        <v-flex xs12 md4 justify="center">
          <v-card outlined height=100%>
            <v-img src="../assets/medal.webp" height="200"></v-img>
            <v-card-title>Top Performer Board</v-card-title>
            <v-card-text>
              <p v-for="(username, index) in rank" :key="index" :class="{'yellow--text text--darken-4': index < 3}">
                {{ index + 1 }}. {{ username }}
              </p>
            </v-card-text>
          </v-card>
        </v-flex>
      </v-layout>
    </v-container>
    <v-container>
      <v-tabs>
        <v-tab>Issues</v-tab>
        <v-tab>Monitoring</v-tab>
        <v-tab>Attachment</v-tab>
        <v-tab-item>
          <v-container>
            <Issues :data="data"></Issues>
          </v-container>
        </v-tab-item>
        <v-tab-item>
          <v-container>
            <v-container class="d-flex justify-end">
              <v-btn :href="`/api/${projectId}/charts`" target="blank" color="primary" icon>
                <v-icon>mdi-open-in-new</v-icon>
              </v-btn>
            </v-container>
            <Charts :projectId="projectId"/>
          </v-container>
        </v-tab-item>
        <v-tab-item>
          <v-container>
            <Attachment :projectId="projectId"/>
          </v-container>
        </v-tab-item>
      </v-tabs>
    </v-container>

    <v-dialog v-model="dialog" persistent max-width="600px">
      <ProjectForm
          :project="getProject"
          @toggleDialog="toggleDialog"
          @toggleForbiddenDialog="toggleForbiddenDialog"
          @show-snackbar="toggleSnackbar"
          :data="data"
      />
    </v-dialog>
    <ConfirmDelete
        @toggleDeleteDialog="toggleDeleteDialog"
        :showDialog="confirmDeleteDialog"
    />
    <Forbidden :dialog="forbiddenDialog" @closeDialog="closeForbiddenDialog"/>
    <Snackbar :snackbar="snackbar" :text="message" @close-snackbar="closeSnackbar"/>
  </v-container>
</template>

<script>
import Issues from "../components/Issues";
import ProjectForm from "../components/ProjectForm";
import ConfirmDelete from "../components/ConfirmDelete";
import Forbidden from "../components/Forbidden";
import Charts from "../components/Charts";
import Attachment from "../components/Attachment";
import Snackbar from "../components/Snackbar";

export default {
  data() {
    return {
      projectId: 0,
      project: null,
      showIssues: false,
      dialog: false,
      confirmDeleteDialog: false,
      forbiddenDialog: false,
      snackbar: false,
      message: null,
      rank: []
    };
  },
  setup() {
  },
  props: {
    data: Object,
  },
  created() {
    this.projectId = this.$route.query.projectId;
    this.fetchProject();
    fetch(`/api/${this.projectId}/rank/data`)
        .then(res => {
          if (res.status == 200) {
            return res.json();
          } else {
            return null;
          }
        })
        .then(data => {
          Object.keys(data).forEach(key => {
            if (this.rank.length < 5) this.rank.push(key);
          })
        })
  },
  methods: {
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
              this.toggleSnackbar("Delete successful")
              setTimeout(() => this.$router.push({name: "Projects"}), 1500);
            } else if (res.status == 403) {
              this.forbiddenDialog = true;
            } else {
              console.log("delete unsuccessful");
              this.toggleSnackbar("Delete unsuccessful")
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
            if (data) {
              this.project = data;
            }
          })
          .catch((e) => console.log(e));
    },
    closeForbiddenDialog() {
      this.forbiddenDialog = false;
    },
    toggleForbiddenDialog() {
      console.log("toggle")
      this.forbiddenDialog = true;
    },
    toggleSnackbar(text) {
      this.snackbar = true;
      this.message = text;
    },
    closeSnackbar() {
      this.snackbar = false;
      this.message = null;
    },
  },
  components: {
    Snackbar,
    Issues,
    ProjectForm,
    ConfirmDelete,
    Forbidden,
    Charts,
    Attachment
  },
  computed: {
    getProject() {
      return this.project;
    },
    getProjectDescription() {
      return `<p>${this.project.description.replace(/\n/g, "<br/>")}</p>`;
    }
  },
};
</script>

<style scoped>
</style>