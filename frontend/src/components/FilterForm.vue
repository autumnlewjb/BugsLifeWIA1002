<template>
  <v-container>
    <v-row>
      <v-col>
        <v-form>
          <v-row>
            <v-col sm="12" md="12">
              <v-combobox solo label="Issue Tags" v-model="filterTags" :items="tagOptions" multiple></v-combobox>
            </v-col>
          </v-row>
          <v-row>
            <v-col sm="12" md="12">
              <v-combobox solo label="Issue Status" v-model="filterStatus" :items="filterOptions"></v-combobox>
            </v-col>
          </v-row>
          <v-row class="d-flex justify-end">
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
      filterTags: [],
      filterStatus: null,
      filterTagsArray: [],
      filterStatusArray: [],
      tagOptions: ['Frontend', 'Backend', 'Suggestion', 'First Bug', 'Enhancement'],
      filterOptions: ['Open', 'Resolved', 'Closed', 'In Progress', 'Reopened']
    }
  },
  created() {
    this.filterTagsArray = this.tags;
    this.filterStatusArray = this.status;
    this.filterTagsArray.forEach(element => this.filterTags.push(element));
    this.filterStatusArray.forEach(element => this.filterStatus.push(element));
  },
  watch: {
    filterTags(val) {
      const len = this.filterTagsArray.length;
      for (var i = 0; i < len; i++) {
        this.filterTagsArray.pop();
      }
      val?.forEach(element => this.filterTagsArray.push(element));
    },
    filterStatus(val) {
      const len = this.filterStatusArray.length;
      for (var i = 0; i < len; i++) {
        this.filterStatusArray.pop();
      }
      if (val != null) {
        this.filterStatusArray.push(val);
      } else {
        this.filterStatusArray = [];
      }
    }
  },
  computed: {},
  methods: {
    clearAll() {
      this.filterTags = [];
      this.filterStatus = null;
    },
  }
}
</script>