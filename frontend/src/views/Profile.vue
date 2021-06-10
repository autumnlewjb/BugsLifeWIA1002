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
            <v-col cols="12" md="10" sm="10">
              <h3>{{ getUser.username }}</h3>
            </v-col>
            <v-col cols="12" md="2" sm="2">
              <v-btn icon @click="showEditProfile = true">
                <v-icon>mdi-pencil</v-icon>
              </v-btn>
            </v-col>
          </v-row>
          <v-row class="mt-0">
            <v-col>
              <p>{{ getUser.email }}</p>
            </v-col>
          </v-row>
        </v-container>
        <v-container v-if="isAdmin">
          <v-card outlined>
            <v-card-title> Administrative Control</v-card-title>
            <v-list dense>
              <v-list-item-group>
                <v-list-item @click="handleExportJson" target="blank">
                  <v-list-item-icon>
                    <v-icon>mdi-export</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title>Export Database</v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
                <v-list-item :to="{ name: 'Register' }">
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
                <v-row>
                  <v-col>
                    <v-card-title>{{ project.name }}</v-card-title>
                  </v-col>
                  <v-col cols="2">
                    <p class="mr-4 text-right text-h6 grey--text">#{{ project.projectId }}</p>
                  </v-col>
                </v-row>
                <v-card-text
                    v-html="getDescription(project.description)"
                ></v-card-text>
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
            </v-container>
            <p style="text-align: center" class="text--secondary pa-5" v-else>
              Hhhhmmmm... You did not create a project...
            </p>
          </v-tab-item>
          <v-tab-item>
            <v-container v-if="issues.length > 0">
              <v-card v-for="issue in issues" :key="issue.id" class="ma-5">
                <v-card-title>
                  <span
                      class="mr-5 status"
                      :style="`min-width: 1rem; background-color: ${
                      statusColor[issue.status]
                    }`"
                  >{{ issue.status }}</span
                  >
                  {{ issue.title }}
                  <v-icon v-for="n in issue.priority" :key="n" color="red"
                  >mdi-exclamation
                  </v-icon
                  >
                </v-card-title>
                <v-card-text>
                  <v-chip
                      v-for="(tag, index) in issue.tag"
                      :key="index"
                      class="mx-1"
                  >{{ tag }}
                  </v-chip
                  >
                </v-card-text>
                <v-card-text
                    v-html="
                    getDescription(
                      issue.descriptionText == null ||
                        issue.descriptionText == ''
                        ? '(Description not specified)'
                        : issue.descriptionText
                    )
                  "
                >
                </v-card-text>
                <v-card-text>
                  Created on
                  {{
                    issue.timestamp == null
                        ? "(Not Specified)"
                        : new Date(issue.timestamp).toLocaleString()
                  }}
                </v-card-text>
                <v-card-actions>
                  <v-btn
                      text
                      color="primary"
                      :to="{
                      name: 'Issue',
                      query: {
                        projectId: issue.projectId,
                        issueId: issue.issueId,
                      },
                    }"
                  >
                    View Issue
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-container>
            <p style="text-align: center" class="text--secondary pa-5" v-else>
              Yessshhh!!! All clear...
            </p>
          </v-tab-item>
          <v-tab-item>
            <v-container v-if="assigned.length > 0">
              <v-card v-for="issue in assigned" :key="issue.id" class="ma-5">
                <v-card-title>
                  <span
                      class="mr-5 status"
                      :style="`min-width: 1rem; background-color: ${
                      statusColor[issue.status]
                    }`"
                  >{{ issue.status }}</span
                  >
                  {{ issue.title }}
                  <v-icon v-for="n in issue.priority" :key="n" color="red"
                  >mdi-exclamation
                  </v-icon
                  >
                </v-card-title>
                <v-card-text>
                  <v-chip
                      v-for="(tag, index) in issue.tag"
                      :key="index"
                      class="mx-1"
                  >{{ tag }}
                  </v-chip
                  >
                </v-card-text>
                <v-card-text
                    v-html="
                    getDescription(
                      issue.descriptionText == null ||
                        issue.descriptionText == ''
                        ? '(Description not specified)'
                        : issue.descriptionText
                    )
                  "
                >
                </v-card-text>
                <v-card-text>
                  Created on
                  {{
                    issue.timestamp == null
                        ? "(Not Specified)"
                        : new Date(issue.timestamp).toLocaleString()
                  }}
                </v-card-text>
                <v-card-actions>
                  <v-btn
                      text
                      color="primary"
                      :to="{
                      name: 'Issue',
                      query: {
                        projectId: issue.projectId,
                        issueId: issue.issueId,
                      },
                    }"
                  >
                    View Issue
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-container>
            <p style="text-align: center" class="text--secondary pa-5" v-else>
              No assigned issue... yet.
            </p>
          </v-tab-item>
        </v-tabs>
      </v-flex>
    </v-layout>
    <v-dialog v-model="showEditProfile">
      <v-card>
        <v-container>
          <v-row>
            <h1 class="">Edit Profile</h1>
          </v-row>
          <v-row class="justify-center align-center">
            <v-col cols="12">
              <v-form @submit="handleEdit">
                <v-text-field
                    type="email"
                    name="email"
                    v-model="email"
                    outlined
                    color="teal"
                    label="Email"
                />
                <v-text-field
                    :type="hide ? 'text' : 'password'"
                    name="password"
                    v-model="password"
                    outlined
                    color="teal"
                    :append-icon="hide ? 'mdi-eye' : 'mdi-eye-off'"
                    @click:append="hide = !hide"
                    label="Password"
                />
              </v-form>
            </v-col>
            <v-col cols="8" class="d-flex child-flex justify-center">
              <v-btn type="submit" color="teal" dark @click="handleEdit">Save Edits</v-btn>
            </v-col>
          </v-row>
          <!-- <Form :collectEmail="true" :collectUsername="false" formPurpose="Edit Profile" @form-submit="handleEdit" :user="getUser"/> -->
        </v-container>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>

export default {
  name: "Profile",
  setup() {
  },
  components: {},
  data() {
    return {
      user: null,
      isAdmin: false,
      projects: [],
      issues: [],
      assigned: [],
      statusColor: {
        Open: "#7eb67e",
        Closed: "#afabab",
        Resolved: "#f8f899",
        "In Progress": "#93dcdf",
        Reopened: "#dfa44d",
      },
      showEditProfile: false,
      email: '',
      password: '',
      hide: false,
    };
  },
  watch: {
    user(val) {
      this.email = val.email;
      this.password = val.password;
    },
    projects(val) {
      console.log(val);
    }
  },
  async created() {
    console.log("created profile");
    this.user = this.$store.getters.getCurrentUser;
    this.projects = this.user.project;
    await fetch(`/api/user`)
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
    await fetch(`/api/`)
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
          );
          projectIssues.forEach(
            (issue) => (issue.projectId = project.projectId)
          );
          assignedIssues.forEach(
            (issue) => (issue.projectId = project.projectId)
          );
          this.issues = [...this.issues, ...projectIssues];
          this.assigned = [...this.assigned, ...assignedIssues];
        })
      });
    this.isAdmin =
      this.user?.roles.find((role) => role.name == "ADMIN") != null;
    console.log("sorted");
    this.projects.sort((a, b) => {
      console.log(new Date(b.date).getTime() - new Date(a.date).getTime());
      return new Date(b.date).getTime() - new Date(a.date).getTime();
    });
    console.log(this.projects);
    this.issues.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp).getTime());
    this.assigned.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp).getTime());
    console.log("done");
  },
  methods: {
    getDescription(str) {
      var tmp = document.createElement("DIV");
      tmp.innerHTML = str;
      return tmp.textContent || tmp.innerText || "";
    },
    handleEdit() {
      fetch(`/api/user`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username: this.user.username,
          email: this.email,
          password: this.password
        }),
      })
          .then((res) => {
            if (res.status == 200) {
              this.showEditProfile = false;
              this.$store.commit('setSuccessEdit', 'Profile');
              return res.json();
            }
          })
          .then((data) => {
            if (data) {
              this.$store.dispatch("fetchCurrentUser");
            }
          })
          .catch((e) => console.log(e));
    },
    handleExportJson() {
      fetch(`/api/JSON`)
          .then(res => {
            if (res.status == 200) {
              return res.json();
            } else {
              return null;
            }
          })
          .then(data => {
            if (data) {
              var dataStr = "data:text/json; charset=utf-8," + encodeURIComponent(JSON.stringify(data));
              var downloadElement = document.createElement('a');
              downloadElement.setAttribute('href', dataStr);
              downloadElement.setAttribute('download', 'export.json');
              document.body.appendChild(downloadElement);
              downloadElement.click();
              downloadElement.remove();
            }
          })
    }
  },
  computed: {
    getUser() {
      return this.$store.getters.getCurrentUser ? this.$store.getters.getCurrentUser : {};
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