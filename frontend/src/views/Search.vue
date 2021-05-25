<template>
  <v-container>
    <v-row class="d-flex justify-center">
      <v-col cols="12" sm="12" md="8">
        <v-combobox
          placeholder="Search for projects, issues and more"
          filled
          rounded
          prepend-icon="mdi-magnify"
          append-icon=""
          :search-input.sync="search"
          :hide-no-data="true"
          :loading="isLoading"
          autofocus
        ></v-combobox>
      </v-col>
    </v-row>
    <v-container v-if="items.length > 0" class="min-height: 500px">
      <v-row class="d-flex justify-center">
        <v-col cols="12" sm="12" md="10">
          <v-list three-line min-height="500">
            <v-list-item
              v-for="(item, index) in getItems"
              :key="index"
              @click="handleClick(item.id, item.route)"
              rounded
            >
              <v-list-item-content>
                <v-list-item-title v-html="item.name"></v-list-item-title>
                <v-list-item-subtitle
                  v-html="item.description"
                ></v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-col>
      </v-row>
      <v-row class="d-flex justify-center">
        <v-col cols="12" sm="12" md="8">
            <v-pagination v-model="page" :length="totalPages"></v-pagination>
        </v-col>
      </v-row>
    </v-container>
    <v-container v-if="items == 0" class="d-flex justify-center">
      <p><i>Start your search with</i> <strong>issue:</strong> <i>or</i> <strong>project:</strong> <i>or</i> <strong>user:</strong> <i> for specific search...</i></p>
    </v-container>
  </v-container>
</template>

<script>
export default {
  setup() {},
  data() {
    return {
      search: null,
      items: [],
      totalPages: 0,
      isLoading: false,
      page: 1,
      searchType: "",
      query: ""
    };
  },
  watch: {
    search(val) {
      if (val == null) {
        return;
      }
      if (this.isLoading) {
        return;
      }

      const focus = val.substring(0, Math.max(val.indexOf(":"), 0)).trim().toLowerCase();
      const query = val.substring(Math.max(val.indexOf(":") + 1, 0)).trim().toLowerCase();
      console.log(focus, query);

      this.isLoading = true;
      var url;
      if (focus === "project") {
        url = `/api/searchProject?query=${query}&size=5&page=0`;
        this.searchType = "project";
      } else if (focus == 'issue') {
        url = `/api/searchIssue?query=${query}&size=5&page=0`;
        this.searchType="issue";
      } else if (focus == 'user') {
        url = `/api/search?query=${query}&size=5&page=0`;
        this.searchType = "user";
      } else {
        url = `/api?query=${query}&size=5&page=0`;
        this.searchType="all";
      }
      fetch(url)
          .then((res) => {
            if (res.status == 200) {
              return res.json();
            } else {
              return {
                content: [],
                totalPages: 0,
              };
            }
          })
          .then((data) => {
            this.isLoading = false;
            this.query = query;
            this.page = 1;
            const { content, totalPages } = data;
            this.totalPages = totalPages;
            this.items = content;
          });
    },
    page(val) {
        console.log(val);
        var url;
        if (this.searchType === "project") {
          url = `/api/searchProject`;
        } else if (this.searchType == 'issue') {
          url = `/api/searchIssue`;
        } else if (this.searchType == 'user') {
          url = `/api/search`;
        } else {
          url = `/api`;
        }
        fetch(`${url}?query=${this.query}&size=5&page=${val-1}`)
        .then((res) => {
          if (res.status == 200) {
            return res.json();
          } else {
            return {
              content: [],
              totalPages: 0,
            };
          }
        })
        .then((data) => {
            const { content, totalPages, number } = data;
            console.log(content);
            this.page = number+1;
            this.totalPages = totalPages;
            this.items = content;
        });
    },
  },
  computed: {
    getItems() {
      this.items.map((item) => {
        if (item.project_id) {
          item.id = item.project_id;
          item.route = "Project"
        } else if (item.issue_id) {
          item.id = item.issue_id;
          item.route = "Issue";
          item.name = item.title;
          item.description = item.descriptionText;
        } else if (item.user_id) {
          item.name = item.username;
          item.description = item.email;
        }
      });

      return this.items;
    },
  },
  methods: {
    handleClick(id, routeName) {
      if (routeName == "Project") {
        this.$router.push({ name: routeName, query: {projectId: id}});
      } else if (routeName == "Issue") {
        this.$router.push({ name: routeName, query: {issueId: id}});
      }
    },
  },
};
</script>