<template>
  <v-container>
    <v-layout row>
      <v-flex md4>
        <v-container justify="center">
            <v-row>
                <v-avatar size="200" color="primary" rounded>
                    <span class="white--text text-h1">{{
                    user.username[0].toUpperCase()
                    }}</span>
                </v-avatar>
            </v-row>
            <v-row class="mt-16">
                <h3>{{ user.username }}</h3>
            </v-row>
            <v-row>
                <p>{{ user.email }}</p>
            </v-row>
        </v-container>
        <v-container v-if="isAdmin">
            <v-card outlined>
                <v-card-title>
                    Administrative Control
                </v-card-title>
                <v-list dense>
                    <v-list-item-group>
                        <v-list-item href="/api/JSON" target="blank">
                            <v-list-item-icon>
                                <v-icon>mdi-export</v-icon>
                            </v-list-item-icon>
                            <v-list-item-content>
                                <v-list-item-title>Export Database</v-list-item-title>
                            </v-list-item-content>
                        </v-list-item>
                        <v-list-item :to="{name: 'Register'}">
                            <v-list-item-icon>
                                <v-icon>mdi-account-plus</v-icon>
                            </v-list-item-icon>
                            <v-list-item-content>
                                <v-list-item-title>Register New User</v-list-item-title>
                            </v-list-item-content>
                        </v-list-item>
                    </v-list-item-group>
                </v-list>
            </v-card>
        </v-container>
      </v-flex>
      <v-flex md8>
        <v-tabs>
          <v-tab>My Projects</v-tab>
          <v-tab>My Issues</v-tab>
          <v-tab>Assigned to you</v-tab>
          <v-tab-item>
            <v-container fluid v-if="projects.length > 0">
              <v-card
                v-for="project in projects"
                :key="project.id"
                class="pa-5 ma-5"
              >
                <v-layout row align-center>
                  <v-card-title>{{ project.name }}</v-card-title>
                  <v-card-text
                    v-html="getDescription(project.description)"
                  ></v-card-text>
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
            </v-container>
            <p style="text-align: center;" class="text--secondary pa-5" v-else>Hhhhmmmm... You did not create a project... </p>
          </v-tab-item>
          <v-tab-item>
            <v-container v-if="issues.length > 0">
                <v-card v-for="issue in issues" :key="issue.id" class="ma-5">
                    <v-card-title>
                        <span class="mr-5 status" :style="`min-width: 1rem; background-color: ${statusColor[issue.status]}`">{{
                        issue.status
                        }}</span>
                        {{ issue.title }}
                        <v-icon v-for="n in issue.priority" :key="n" color="red"
                        >mdi-exclamation</v-icon
                        >
                    </v-card-title>
                    <v-card-text>
                            <v-chip v-for="(tag, index) in issue.tag" :key="index"
                            class="mx-1"
                            >{{ tag }}</v-chip
                            >
                    </v-card-text>
                    <v-card-text
                        v-html="
                        getDescription(
                            issue.descriptionText == null || issue.descriptionText == ''
                            ? '(Description not specified)'
                            : issue.descriptionText
                        )
                        "
                    >
                    </v-card-text>
                    <v-card-text>
                        Created on
                        {{
                        issue.timestamp == null ? "(Not Specified)" : issue.timestamp
                        }}
                    </v-card-text>
                    <v-card-actions>
                        <v-btn
                        text
                        color="primary"
                        :to="{
                            name: 'Issue',
                            query: { projectId: issue.projectId, issueId: issue.issueId },
                        }"
                        >
                        View Issue
                        </v-btn>
                    </v-card-actions>
                </v-card>
            </v-container>
            <p style="text-align: center;" class="text--secondary pa-5" v-else>Yessshhh!!! All clear... </p>
          </v-tab-item>
          <v-tab-item>
              <v-container v-if="assigned.length > 0">
                <v-card v-for="issue in assigned" :key="issue.id" class="ma-5">
                    <v-card-title>
                        <span class="mr-5 status" :style="`min-width: 1rem; background-color: ${statusColor[issue.status]}`">{{
                        issue.status
                        }}</span>
                        {{ issue.title }}
                        <v-icon v-for="n in issue.priority" :key="n" color="red"
                        >mdi-exclamation</v-icon
                        >
                    </v-card-title>
                    <v-card-text>
                            <v-chip v-for="(tag, index) in issue.tag" :key="index"
                            class="mx-1"
                            >{{ tag }}</v-chip
                            >
                    </v-card-text>
                    <v-card-text
                        v-html="
                        getDescription(
                            issue.descriptionText == null || issue.descriptionText == ''
                            ? '(Description not specified)'
                            : issue.descriptionText
                        )
                        "
                    >
                    </v-card-text>
                    <v-card-text>
                        Created on
                        {{
                        issue.timestamp == null ? "(Not Specified)" : issue.timestamp
                        }}
                    </v-card-text>
                    <v-card-actions>
                        <v-btn
                        text
                        color="primary"
                        :to="{
                            name: 'Issue',
                            query: { projectId: issue.projectId, issueId: issue.issueId },
                        }"
                        >
                        View Issue
                        </v-btn>
                    </v-card-actions>
                </v-card>
              </v-container>
              <p style="text-align: center;" class="text--secondary pa-5" v-else>No assigned issue... yet.</p>
          </v-tab-item>
        </v-tabs>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
export default {
  name: "Profile",
  setup() {},
  data() {
    return {
      user: this.$store.getters.getCurrentUser,
      isAdmin: false,
      projects: this.user?.project,
      issues: [],
      assigned: [],
      statusColor: {
        'Open': '#7eb67e',
        'Closed': '#afabab',
        'Resolved': '#f8f899',
        'In Progress': '#93dcdf',
        'Reopened': '#dfa44d'
      }
    };
  },
  created() {
    fetch(`/api/user`)
      .then((res) => {
        if (res.status == 200) {
          return res.json();
        } else {
          return null;
        }
      })
      .then((data) => {
        if (data) {
          console.log("success");
          this.user = data;
          this.projects = data.project;
        }
        console.log(this.user);
      });
    fetch(`/api/`)
      .then((res) => {
        if (res.status == 200) {
          return res.json();
        } else {
          return [];
        }
      })
      .then((data) => {
        console.log(data);
        this.issues = [];
        this.assigned = [];
        data.forEach((project) => {
            const projectIssues = project.issue.filter(
              (issue) => issue.createdBy == this.user.username
            );
            const assignedIssues = project.issue.filter(
                (issue) => issue.assignee == this.user.username
            )
            projectIssues.forEach((issue) => issue.projectId = project.projectId);
            assignedIssues.forEach((issue) => issue.projectId = project.projectId);
          this.issues = [
            ... this.issues,
            ... projectIssues
          ];
          this.assigned = [... this.assigned, ... assignedIssues];
        });
      });
      this.isAdmin = this.user?.roles.find((role) => role.name == 'ADMIN') != null;
  },
  methods: {
    getDescription(str) {
      var tmp = document.createElement("DIV");
      tmp.innerHTML = str;
      return tmp.textContent || tmp.innerText || "";
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