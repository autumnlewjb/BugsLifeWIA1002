<template>
  <v-container>
    <v-container style="min-height: 80vh">
      <v-layout row justify-space-around class="my-10">
        <v-flex xs12 md8>
          <h1 class="subheading">Project Dashboard
            <v-tooltip bottom>
              <template v-slot:activator="{ on, attrs }">
                <v-btn plain icon @click="handleClick('sort')" :color="getSortButtonColor" v-bind="attrs" v-on="on">
                  <v-icon>mdi-sort</v-icon>
                </v-btn>
              </template>
              <span>Sort</span>
            </v-tooltip>
            <!-- <v-btn plain icon @click="handleClick('filter')" disabled>
              <v-icon>mdi-filter</v-icon>
            </v-btn> -->
          </h1>
        </v-flex>
        <v-flex xs12 md2>
          <v-btn
              elevation="3"
              rounded
              color="teal"
              class="white--text"
              @click="dialog = true"
              align="center" justify="center"
          >+ Add Project
          </v-btn
          >
        </v-flex>
      </v-layout>
      <v-layout row justify-content-center>
        <v-flex xs12 md12 v-if="showFilterForm">
          <SingleFilter/>
        </v-flex>
        <v-flex xs12 md12 v-show="showSortForm">
          <SingleSort :sortSubjects="sortSubjects" :alreadyInSort="sortData" v-if="!multipleSort"/>
          <SortForm :sortSubjects="sortSubjects" :alreadyInSort="sortData" v-if="multipleSort"/>
        </v-flex>
      </v-layout>
      <v-layout row>
        <v-flex md12 sm12 v-if="getProjects.length == 0" justify-center class="ma-10">
          <p style="text-align: center;" class="text--secondary">Hhhhmmmm... No projects now</p>
        </v-flex>
        <v-flex xs12 md12 v-else>
          <v-card
              v-for="project in getProjects"
              :key="project.id"
              class="pa-5 ma-5"
          >
            <v-row>
              <v-col>
                <v-card-title>{{ project.name }}</v-card-title>
              </v-col>
              <v-col cols="2">
                <p class="mr-4 text-right text-h6 grey--text">#{{ project.projectId }}</p>
              </v-col>
            </v-row>
            <v-card-text v-html="getDescription(project.description)"></v-card-text>
            <v-card-text
            >Created on
              {{
                project.date == null ? "(Not Specified)" : new Date(project.date).toLocaleString()
              }}
            </v-card-text
            >
            <v-card-actions class="d-flex justify-end">
              <v-btn
                  color="primary"
                  :to="{
                    path: 'project',
                    query: { projectId: project.projectId },
                  }"
                  text
              >View Project
              </v-btn
              >
            </v-card-actions>
          </v-card>
        </v-flex>
      </v-layout>
    </v-container>
    <v-row justify="center">
      <v-dialog v-model="dialog" persistent max-width="600px">
        <ProjectForm @toggleDialog="toggleDialog"
                     @show-snackbar="toggleSnackbar"/>
      </v-dialog>
    </v-row>
    <Snackbar :snackbar="snackbar" :text="message" @close-snackbar="closeSnackbar"/>
  </v-container>
</template>

<script>
import ProjectForm from "../components/ProjectForm";
import SingleFilter from "../components/SingleFilter";
import SingleSort from "../components/SingleSort";
import SortForm from "../components/SortForm";
import Snackbar from "../components/Snackbar";

export default {
  data() {
    return {
      hidden: false,
      dialog: false,
      projects: [],
      showFilterForm: false,
      showSortForm: false,
      filterActive: false,
      sortActive: false,
      sortData: [],
      sortSubjects: [
        {
          text: 'Name',
          value: 'name'
        },
        {
          text: 'Date',
          value: 'date'
        },
        {
          text: 'Number Of Issues',
          value: 'issueNum'
        },
        {
          text: 'Project ID',
          value: 'projectId'
        }
      ],
      tags: [],
      status: [],
      multipleSort: true,
      snackbar: false,
      message: null
    };
  },
  created() {
    this.fetchProjects(`/api/`);
  },
  components: {
    ProjectForm,
    SingleFilter,
    SingleSort,
    SortForm,
    Snackbar
  }
  ,
  watch: {
    sortData(val) {
      var url;
      if (val.length > 0) {
        url = `/api/?`;
        val.forEach((element) => url += `&sort=${element.subject},${element.order}`)
        this.sortActive = true;
      } else {
        url = `/api/`;
        this.sortActive = false;
      }
      this.fetchProjects(url);
    }
  },
  methods: {
    toggleDialog() {
      this.fetchProjects(`/api/`);
      this.dialog = false;
    },
    fetchProjects(url) {
      fetch(url)
          .then((res) => {
            if (res.status == 200) {
              return res.json();
            } else {
              return [];
            }
          })
          .then((data) => {
            this.projects = data;
          })
          .catch((e) => console.log(e));
    },
    handleClick(item) {
      if (item == 'sort') {
        this.showSortForm = !this.showSortForm;
        this.showFilterForm = false;
      } else {
        this.showFilterForm = !this.showFilterForm;
        this.showSortForm = false;
      }
    },
    getDescription(str) {
      var tmp = document.createElement("DIV");
      tmp.innerHTML = str;
      return tmp.textContent || tmp.innerText || "";
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
  computed: {
    getProjects() {
      return this.projects;
    },
    getSortButtonColor() {
      if (this.sortActive) {
        return "amber darken-4";
      } else if (this.showSortForm) {
        return "primary";
      } else {
        return "undefined";
      }
    },
    getFilterButtonColor() {
      return this.showFilterForm && this.filterActive;
    }
  },
};
</script>

<style scoped>
</style>