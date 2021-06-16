<template>
  <div>
    <v-container>
      <v-layout row class="my-10">
        <v-flex xs12 md10>
          <h2 class="subheading">
            <v-tooltip bottom>
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                    plain
                    icon
                    @click="handleClick('sort')"
                    :color="getSortButtonColor"
                    v-bind="attrs"
                    v-on="on"
                >
                  <v-icon>mdi-sort</v-icon>
                </v-btn>
              </template>
              <span>Sort</span>
            </v-tooltip>
            <v-tooltip bottom>
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                    plain
                    icon
                    @click="handleClick('filter')"
                    :color="getFilterButtonColor"
                    v-on="on"
                    v-bind="attrs"
                >
                  <v-icon>mdi-filter</v-icon>
                </v-btn>
              </template>
              <span>Filter</span>
            </v-tooltip>
          </h2>
        </v-flex>
        <v-flex xs12 md2 class="d-flex justify-end">
          <v-btn
              elevation="3"
              rounded
              color="teal"
              class="white--text"
              @click="dialog = true"
          >+ Add Issue
          </v-btn
          >
        </v-flex>
      </v-layout>
      <v-layout row justify-content-center>
        <v-flex xs12 md12 v-show="showFilterForm">
          <SingleFilter
              :filterSubjects="filterSubjects"
              :tags="tags"
              :status="status"
              v-if="!multipleFilterAndSort"
          />
          <FilterForm
              :filterSubjects="filterSubjects"
              :tags="tags"
              :status="status"
              v-if="multipleFilterAndSort"
          />
        </v-flex>
        <v-flex xs12 md12 v-show="showSortForm">
          <SingleSort
              :sortSubjects="sortSubjects"
              :alreadyInSort="sortData"
              v-if="!multipleFilterAndSort"
          />
          <SortForm
              :sortSubjects="sortSubjects"
              :alreadyInSort="sortData"
              v-if="multipleFilterAndSort"
          />
        </v-flex>
      </v-layout>
    </v-container>
    <v-container>
      <v-layout row>
        <v-flex xs12 md12 v-if="getIssues != null && getIssues.length > 0">
          <v-card v-for="issue in getIssues" :key="issue.id" class="pa-5 ma-5">
            <v-card-title class="ma-0 pa-0">
              <v-col cols="10">
                <v-card-title class="mb-0 pb-0">
                    <span class="status mr-3"
                          :style="`min-width: 1rem; background-color: ${statusColor[issue.status]}`">
                      {{ issue.status }}
                    </span>
                  <v-icon v-for="n in issue.priority" :key="n" color="red">
                    mdi-exclamation
                  </v-icon>
                </v-card-title>
              </v-col>
              <v-col cols="2">
                <p class="mr-4 text-right text-h6 grey--text">#{{ issue.issueId }}</p>
              </v-col>
            </v-card-title>
            <v-card-subtitle class="pb-0">
              <v-col cols="12">
                <v-chip-group v-if="issue.tag">
                  <v-tooltip bottom v-for="tag in issue.tag" :key="tag">
                    <template v-slot:activator="{ on, attrs }">
                      <v-chip
                          class="mx-1"
                          @click="handleChipClick(tag)"
                          v-on="on"
                          v-bind="attrs"
                      >{{ tag }}
                      </v-chip
                      >
                    </template>
                    <span>Filter by "{{ tag }}"</span>
                  </v-tooltip>
                </v-chip-group>
              </v-col>
            </v-card-subtitle>
            <v-container class="pl-6 pt-0">
              <v-card-title>
                {{ issue.title }}
              </v-card-title>
              <v-card-text v-html="getDescription(issue.descriptionText == null || issue.descriptionText == ''
                  ? '(Description not specified)'
                  : issue.descriptionText)">
              </v-card-text
              >
              <v-card-text>
                Created on
                {{
                  issue.timestamp == null ? "(Not Specified)" : new Date(issue.timestamp).toLocaleString()
                }}
              </v-card-text>
            </v-container>
            <v-card-actions class="d-flex justify-end">
              <v-btn
                  text
                  color="primary"
                  :to="{
                  name: 'Issue',
                  query: { projectId: projectId, issueId: issue.issueId },
                }"
              >
                View Issue
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-flex>
        <v-flex md12 sm12 v-else justify-center class="ma-10">
          <p style="text-align: center;" class="text--secondary">Hhhhmmmm... No issues now</p>
        </v-flex>
      </v-layout>
    </v-container>
    <v-dialog v-model="dialog" persistent max-width="600px">
      <IssueForm
          @toggleDialog="toggleDialog"
          @show-snackbar="toggleSnackbar"
          :data="data"
          :projectId="projectId"
      />
    </v-dialog>
    <Snackbar :snackbar="snackbar" :text="message" @close-snackbar="closeSnackbar"/>
  </div>
</template>

<script>
import IssueForm from "../components/IssueForm";
import SingleFilter from "../components/SingleFilter";
import SingleSort from "../components/SingleSort";
import SortForm from "../components/SortForm";
import FilterForm from "../components/FilterForm";
import Snackbar from "./Snackbar";

export default {
  setup() {
  },
  data() {
    return {
      projectId: 0,
      project: null,
      dialog: false,
      issues: null,
      showFilterForm: false,
      showSortForm: false,
      filterActive: false,
      sortActive: false,
      sortData: [],
      // sortSubjects: ['title', 'timestamp', 'priority', 'commentNum'],
      sortSubjects: [
        {
          text: "Title",
          value: "title",
        },
        {
          text: "Timestamp",
          value: "timestamp",
        },
        {
          text: "Priority",
          value: "priority",
        },
        {
          text: "Number Of Comments",
          value: "commentNum",
        },
        {
          text: "Issue ID",
          value: "issueId",
        },
      ],
      tags: [],
      status: [],
      filterSubjects: ["tag", "status"],
      queryParams: {
        sort: [],
        filter: {
          tag: [],
          status: [],
        },
      },
      multipleFilterAndSort: true,
      statusColor: {
        'Open': '#7eb67e',
        'Closed': '#afabab',
        'Resolved': '#f8f899',
        'In Progress': '#93dcdf',
        'Reopened': '#dfa44d'
      },
      snackbar: false,
      message: null
    };
  },
  props: {
    data: Object,
  },
  created() {
    this.projectId = this.$route.query.projectId;
    this.fetchIssues();
  },
  components: {
    IssueForm,
    SingleFilter,
    SingleSort,
    SortForm,
    FilterForm,
    Snackbar
  },
  watch: {
    async sortData(val) {
      this.queryParams.sort = [];
      if (val.length > 0) {
        val.forEach((element) =>
            this.queryParams.sort.push(`${element.subject},${element.order}`)
        );
        this.sortActive = true;
      } else {
        this.sortActive = false;
      }
      await this.fetchIssues();
    },
    async tags(val) {
      this.queryParams.filter.tag = [];
      if (focus) {
        val.forEach((element) =>
            this.queryParams.filter.tag.push(`tag,${element}`)
        );
        this.filterActive = true;
      } else {
        this.filterActive = false;
      }
      await this.fetchIssues();
    },
    async status(val) {
      this.queryParams.filter.status = [];
      if (focus) {
        val.forEach((element) =>
            this.queryParams.filter.status.push(`status,${element}`)
        );
        this.filterActive = true;
      } else {
        this.filterActive = false;
      }
      await this.fetchIssues();
    },
  },
  methods: {
    toggleDialog() {
      this.dialog = !this.dialog;
      this.fetchIssues();
    },
    fetchIssues() {
      const url = this.getRequestUrl();
      fetch(url)
          .then((res) => {
            if (res.status == 200) {
              return res.json();
            } else {
              return null;
            }
          })
          .then((data) => {
            this.issues = data;
          })
          .catch((e) => console.log(e));
    },
    handleClick(item) {
      if (item == "sort") {
        this.showSortForm = !this.showSortForm;
        this.showFilterForm = false;
      } else {
        this.showFilterForm = !this.showFilterForm;
        this.showSortForm = false;
      }
    },
    getRequestUrl() {
      var url = `/api/${this.projectId}`;
      const sort = this.queryParams.sort;
      const filter = [
        ...this.queryParams.filter.tag,
        ...this.queryParams.filter.status,
      ];
      if (sort.length > 0 || filter.length > 0) {
        url = url + "?";
      }

      if (sort.length > 0) {
        sort.forEach((element) => {
          url += `&sort=${element}`;
        });
      }

      if (filter.length > 0) {
        filter.forEach((element) => {
          url += `&filter=${element}`;
        });
      }
      this.sortActive = this.queryParams.sort.length > 0;
      this.filterActive =
          this.queryParams.filter.tag.length +
          this.queryParams.filter.status.length;

      return url;
    },
    handleChipClick(tag) {
      this.tags = [];
      this.tags.push(tag);
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
    getIssues() {
      this.issues?.forEach(obj => {
        obj.status = obj.status == 'Reopened' ? "Open" : obj.status;
      });
      return this.issues;
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
      if (this.filterActive) {
        return "amber darken-4";
      } else if (this.showFilterForm) {
        return "primary";
      } else {
        return "undefined";
      }
    },
  },
};
</script>

<style scoped>
.status {
  display: block;
  background-color: burlywood;
  border-radius: 40px;
  padding: 5px 20px;
  min-width: 500px;
}
</style>