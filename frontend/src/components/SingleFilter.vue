<template>
    <v-container>
      <v-row>
        <v-col>
          <v-form>
            <v-row>
              <v-col sm="4" md="4">
                <v-select solo :items="filterSubjects" label="Filter by" v-model="filterSubject"></v-select>
              </v-col>
              <v-col sm="8" md="8">
                <v-combobox solo label="Value" v-model="filter" :items="getOptions"></v-combobox>
              </v-col>
            </v-row>
            <v-row>
              
            </v-row>
            <v-row class="d-flex justify-end">
              <v-btn text @click="applyFilter">Apply</v-btn>
              <v-btn text @click="clearAll">Clear All</v-btn>
            </v-row>
          </v-form>
        </v-col>
      </v-row>
      <v-row class="my-10">
        <v-divider></v-divider>
      </v-row>
    </v-container>
</template>

<script>
export default {
  props: {
    filterSubjects: Array,
    tags: Array,
    status: Array
  },
  data() {
    return {
      filter: '',
      filterSubject: '',
      filterTagsArray: [],
      filterStatusArray: []
    }
  },
  created() {
    this.filterTagsArray = this.tags;
    this.filterStatusArray = this.status;
    if (this.filterTagsArray.length > 0) {
      this.filter = this.filterTagsArray[0];
      this.filterSubject = "tag";
    } else if (this.filterStatusArray.length > 0) {
      this.filterSubject = "status";
    }
  },
  methods: {
    clearAll() {
      this.filter = '';
      this.filterSubject = '';
      if (this.filterTagsArray.length > 0) this.filterTagsArray.pop();
      if (this.filterStatusArray.length > 0) this.filterStatusArray.pop();
    },
    applyFilter() {
      if (this.filterTagsArray.length > 0) this.filterTagsArray.pop();
      if (this.filterStatusArray.length > 0) this.filterStatusArray.pop();
      if (this.filterSubject.length == 0 || this.filter.length == 0) {
        return;
      }

      if (this.filterSubject == 'tag') {
        this.filterTagsArray.push(this.filter);
      } else {
        this.filterStatusArray.push(this.filter);
      }
    }
  }, 
  computed: {
    getOptions() {
      if (!this.filterSubject) {
        return [];
      }

      if (this.filterSubject == 'tag') {
        return ['Frontend', 'Backend', 'Suggestion', 'First Bug', 'Enhancement'];
      } else {
        return ['Open', 'Resolved', 'Closed', 'In progress', 'Reopened'];
      }
    }
  }
}
</script>