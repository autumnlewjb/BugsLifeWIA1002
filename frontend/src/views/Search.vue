<template>
  <v-container>
    <v-row class="d-flex justify-center" align="center">
      <v-col cols="12" sm="10" md="8">
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
      <v-col cols="12" md="2">
        <v-btn
          icon
          plain
          @click="handleSort"
          :color="getSortColor"
          :disabled="availableSort.length == 0"
        >
          <v-icon>mdi-sort</v-icon>
        </v-btn>
        <v-btn
          icon
          plain
          @click="handleFilter"
          :color="getFilterColor"
          :disabled="availableFilter.length == 0"
        >
          <v-icon>mdi-filter</v-icon>
        </v-btn>
      </v-col>
    </v-row>
    <SortForm
      v-if="sortActive && multipleFilterAndSort"
      :sortSubjects="availableSort"
      :alreadyInSort="sortData"
      @acceptSortQuery="acceptSortQuery"
    />
    <SingleSort
      v-if="sortActive && !multipleFilterAndSort"
      :sortSubjects="availableSort"
      :alreadyInSort="sortData"
      @acceptSortQuery="acceptSortQuery"/>
    <FilterForm v-if="filterActive && multipleFilterAndSort" :filterSubjects="availableFilter" :tags="filterTags" :status="filterStatus"/>
    <SingleFilter
      v-if="filterActive && !multipleFilterAndSort" :filterSubjects="availableFilter" :tags="filterTags" :status="filterStatus"
    />
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
                <v-list-item-title
                  >{{ item.name }}
                  <v-chip class="ma-2" outlined color="indigo" small>{{
                    item.type
                  }}</v-chip></v-list-item-title
                >
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
      
      <p class="no-result">
        Found nothing...<br>
        Start your search with <code>issue:</code> or 
        <code>project:</code> or <code>user:</code>
         for specific search...<br>
         Sorting and filtering are only allowed for <code>issue:</code> and <code>project:</code> searches
      </p>
    </v-container>
  </v-container>
</template>

<script>
import SortForm from "../components/SortForm";
import FilterForm from "../components/FilterForm"
import SingleSort from "../components/SingleSort"
import SingleFilter from '../components/SingleFilter';
export default {
  setup() {},
  components: {
    SortForm,
    FilterForm,
    SingleSort,
    SingleFilter
  },
  data() {
    return {
      search: null,
      items: [],
      totalPages: 0,
      isLoading: false,
      page: 1,
      searchType: "",
      query: "",
      sortActive: false,
      filterActive: false,
      sortData: [],
      availableSort: [],
      sortColor: "",
      availableFilter: [],
      filterTags: [],
      filterStatus: [],
      filterColor: '',
      multipleFilterAndSort: true,
      searchDetails: {
        keyword: '',
        sort: [],
        filter: {
          tag: [],
          status: []
        }
      },
    };
  },
  watch: {
    searchType(val) {
      if (val == 'issue') {
        this.availableSort = [
          {
            text: 'Priority',
            value: 'priority'
          },
          {
            text: 'Timestamp',
            value: 'timestamp'
          }
        ];
        this.availableFilter = ['tag', 'status'];
      } else if (val == 'project') {
        this.availableSort = [
          {
            text: 'Timestamp',
            value: 'timestamp'
          }
        ];
        this.availableFilter = []
      } else {
        this.availableSort = [];
        this.availableFilter = []
      }
      this.sortActive = false;
      this.sortData = [];
      this.filterData = {
        status: [],
        tags: []
      };
      this.filterActive = false;
    },
    search() {
      this.page = 1;
    },
    getSearchObject(val) {
      if (val == null) {
        return;
      }
      if (this.loading) {
        return;
      }
      this.loading = true;
      var url = `/api?query=${val.query}&scope=${val.scope}&size=5&page=${val.page}`;
      this.searchType = val.scope;

      val.sort.forEach((item) => {
        url += `&sort=${item}`;
      });

      val.filter.tag.forEach((item) => {
        url += `&filter=${item}`;
      });

      val.filter.status.forEach((item) => {
        url += `&filter=${item}`;
      });

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
          const { content, totalPages, number } = data;
          console.log(data);
          this.page = number ? number + 1 : 1;
          this.totalPages = totalPages;
          this.items = content;
          this.loading = false;
        });
    }
  },
  computed: {
    getItems() {
      this.items.map((item) => {
        if (item.projectId) {
          item.id = item.projectId;
          item.route = "Project";
          item.type = "project";
        } else if (item.issueId) {
          item.id = item.issueId;
          item.route = "Issue";
          item.type = "issue";
          item.name = item.title;
          item.description = item.descriptionText;
        } else if (item.user_id) {
          item.type = "user";
          item.name = item.username;
          item.description = item.email;
        }
      });

      return this.items;
    },
    getSortColor() {
      if (this.sortData.length > 0) {
        return "amber darken-4";
      } else if (this.sortActive) {
        return "primary";
      }else {
        return "undefined";
      }
    },
    getFilterColor() {
      if (this.filterTags.length > 0 || this.filterStatus.length > 0) {
        return "amber darken-4";
      } else if (this.filterActive) {
        return "primary";
      } else {
        return "undefined"
      }
    },
    getSearchObject() {
      if (this.search == null) {
        return;
      }

      const obj = {
        page: Math.max(this.page - 1, 0),
        query: '',
        scope: '',
        sort: [],
        filter: {
          tag: [],
          status: []
        }
      };

      obj.scope = this.search
        .substring(0, Math.max(this.search.indexOf(":"), 0))
        .trim()
        .toLowerCase();
      obj.query = this.search
        .substring(Math.max(this.search.indexOf(":") + 1, 0))
        .trim()
        .toLowerCase();
      
      if (this.sortData.length > 0 && (this.searchType == "project" || this.searchType == "issue")) {
        this.sortData.forEach((element) => {
          obj.sort.push(`${element.subject},${element.order}`);
        });
      }

      if (this.searchType == "issue") {
        if (this.filterTags.length > 0) {
          this.filterTags.forEach((element) => {
            obj.filter.tag.push(`tag,${element}`);
          });
        }
  
        if (this.filterStatus.length > 0) {
          this.filterStatus.forEach((element) => {
            obj.filter.status.push(`status,${element}`);
          });
        }
      }
      return obj;
    }
  },
  methods: {
    handleClick(id, routeName) {
      if (routeName == "Project") {
        this.$router.push({ name: routeName, query: { projectId: id } });
      } else if (routeName == "Issue") {
        this.$router.push({ name: routeName, query: { issueId: id } });
      }
    },
    handleSort() {
      this.filterActive = false;
      this.sortActive = !this.sortActive;
      if (this.sortData.length > 0) {
        this.sortColor = "amber darken-4";
      } else if (this.sortActive) {
        this.sortColor = "primary";
      }else {
        this.sortColor = "undefined";
      }
    },
    handleFilter() {
      this.sortActive = false;
      this.filterActive = !this.filterActive;
      if (this.filterTags.length > 0 || this.filterStatus.length > 0) {
        this.filterColor = "amber darken-4";
      } else if (this.filterActive) {
        this.filterColor = "primary";
      } else {
        this.filterColor = "undefined"
      }
    },
    acceptSortQuery(sortData) {
      this.sortData = sortData;
    },
  },
};
</script>


<style scoped>
p > span {
  font-size: 15px;
  font-weight: bold;
  background-color: #ada7a791;
  padding: 2px;
}

</style>