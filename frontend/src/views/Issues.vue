<template>
  <div>
    <v-sheet elevation="10" class="ma-5 pa-5" rounded>
      <v-container>
        <v-layout row>
          <v-flex xs12 md8>
            <h1 class="subheading">Issue Dashboard
              <v-btn plain icon @click="handleClick('sort')" :color="getSortButtonColor">
                <v-icon>mdi-sort</v-icon>
              </v-btn>
              <v-btn plain icon @click="handleClick('filter')" :color="getFilterButtonColor">
                <v-icon>mdi-filter</v-icon>
              </v-btn>
            </h1>
          </v-flex>
          <v-flex xs12 md2 class="d-flex justify-end">
            <v-btn
              elevation="3"
              rounded
              color="teal"
              class="white--text"
              @click="dialog = true"
              >+ Add Issue</v-btn
            >
          </v-flex>
        </v-layout>
        <v-layout row justify-content-center>
          <v-flex xs12 md12 v-if="showFilterForm">
            <SingleFilter :filterSubjects="filterSubjects" :tags="tags" :status="status"/>
          </v-flex>
          <v-flex xs12 md12 v-if="showSortForm">
            <SingleSort :sortSubjects="sortSubjects" :alreadyInSort="sortData"/>
          </v-flex>
        </v-layout>
      </v-container>
      <v-container>
        <v-layout row justify-center>
          <v-flex xs12 md12>
            <v-card v-for="issue in issues" :key="issue.id" class="ma-5">
              <v-card-title>
                {{ issue.title }}
                <v-icon v-for="n in issue.priority" :key="n" color="red"
                  >mdi-exclamation</v-icon
                >
              </v-card-title>
              <v-card-text>
                <v-chip-group v-if="issue.tag">
                  <v-chip v-for="tag in issue.tag" :key="tag" class="mx-1">{{ tag }}</v-chip>
                </v-chip-group>
              </v-card-text>
              <v-card-text>
                {{
                  issue.descriptionText == null || issue.descriptionText == ''
                    ? "(Description not specified)"
                    : issue.descriptionText
                }}</v-card-text
              >
              <v-card-text>
                Status: {{ issue.status }}<br />
                Last updated on
                {{ issue.timestamp == null ? "(Not Specified)" : issue.timestamp }}
              </v-card-text>
              <v-card-actions>
                <v-btn
                  text
                  color="primary"
                  :to="{
                    name: 'Issue',
                    query: { projectId: projectId, issueId: issue.issue_id },
                  }"
                >
                  View Issue
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-flex>
        </v-layout>
      </v-container>
      <v-dialog v-model="dialog" persistent max-width="600px">
        <IssueForm
          @toggleDialog="toggleDialog"
          :data="data"
          :projectId="projectId"
        />
      </v-dialog>
    </v-sheet>
  </div>
</template>

<script>
import IssueForm from "../components/IssueForm";
import SingleFilter from "../components/SingleFilter";
import SingleSort from "../components/SingleSort";

export default {
  setup() {},
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
      sortSubjects: ['timestamp', 'priority'],
      tags: [],
      status: [],
      filterSubjects: ['tag', 'status'],
      queryParams: {
        sort: [],
        filter: []
      }
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
    SingleSort
  },
  watch: {
    sortData(val) {
      const focus = val[0];
      this.queryParams.sort = [];
      if (focus) {
        this.queryParams.sort.push(`${focus.subject},${focus.order}`);
        this.sortActive = true;
      } else {
        this.sortActive = false;
      }
      this.fetchIssues();
    },
    tags(val) {
      const focus = val[0];
      this.queryParams.filter = [];
      if (focus) {
        this.queryParams.filter.push(`tag,${focus}`);
        this.filterActive = true;
      } else {
        this.filterActive = false;
      }
      this.fetchIssues();
    },
    status(val) {
      const focus = val[0];
      this.queryParams.filter = [];
      if (focus) {
        this.queryParams.filter.push(`status,${focus}`);
        this.filterActive = true;
      } else {
        this.filterActive = false;
      }
      this.fetchIssues();
    }
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
      if (item == 'sort') {
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
      const filter = this.queryParams.filter;
      if (sort.length > 0 || filter.length > 0) {
        url = url + '?';
      } 

      if (sort.length > 0) {
        sort.forEach(element => {
          url += `&sort=${element}`;
        });
      }

      if (filter.length > 0) {
        filter.forEach((element) => {
          url += `&filter=${element}`;
        })
      }

      return url;
    }
  },
  computed: {
    getIssues() {
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
    }
  },
};
</script>

<style scoped>
</style>