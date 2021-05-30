<template>
  <v-container>
    <v-container style="min-height: 80vh">
      <v-layout row justify-space-around>
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
            >+ Add Project</v-btn
          >
        </v-flex>
      </v-layout>
      <v-layout row justify-content-center>
        <v-flex xs12 md12 v-if="showFilterForm">
          <SingleFilter/>
        </v-flex>
        <v-flex xs12 md12 v-if="showSortForm">
          <SingleSort :sortSubjects="sortSubjects" :alreadyInSort="sortData" v-if="!multipleSort"/>
          <SortForm :sortSubjects="sortSubjects" :alreadyInSort="sortData" v-if="multipleSort"/>
        </v-flex>
      </v-layout>
      <v-layout row>
        <v-flex xs12 md12>
          <v-card
            v-for="project in getProjects"
            :key="project.id"
            class="pa-5 ma-5"
          >
            <v-layout row align-center>
              <v-card-title>{{ project.name }}</v-card-title>
              <v-card-text>{{ project.description }}</v-card-text>
              <v-card-text
                >Created on
                {{
                  project.date == null ? "(Not Specified)" : project.date
                }}</v-card-text
              >
              <v-card-actions class="d-flex justify-end">
                <v-btn
                  color="primary"
                  :to="{
                    path: 'project',
                    query: { projectId: project.projectId },
                  }"
                  text
                  >View Project</v-btn
                >
              </v-card-actions>
            </v-layout>
          </v-card>
        </v-flex>
      </v-layout>
    </v-container>
    <v-row justify="center">
      <v-dialog v-model="dialog" persistent max-width="600px">
        <ProjectForm @toggleDialog="toggleDialog" />
      </v-dialog>
    </v-row>
  </v-container>
</template>

<script>
import ProjectForm from "../components/ProjectForm";
import SingleFilter from "../components/SingleFilter";
import SingleSort from "../components/SingleSort";
import SortForm from "../components/SortForm";

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
      multipleSort: true
    };
  },
  created() {
    this.fetchProjects(`/api/`);
  },
  components: {
    ProjectForm,
    SingleFilter,
    SingleSort,
    SortForm
  },
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
    }
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
  }
};
</script>

<style scoped>
</style>