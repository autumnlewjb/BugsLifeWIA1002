<template>
    <v-container>
      <v-row>
        <v-col>
          <v-form>
            <v-row>
              <v-col sm="12" md="12">
                <v-text-field solo label="Issue Tags" v-model="filterTags"></v-text-field>
              </v-col>
            </v-row>
            <v-row>
              <v-col sm="12" md="12">
                <v-text-field solo label="Issue Status" v-model="filterStatus"></v-text-field>
              </v-col>
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
      filterTags: "",
      filterStatus: "",
      filterTagsArray: [],
      filterStatusArray: []
    }
  },
  created() {
    this.filterTagsArray = this.tags;
    this.filterStatusArray = this.status;
    this.filterTags = this.tags.join(",");
    this.filterStatus = this.status.join(",");
  },
  watch: {
    filterTags(val) {
      const len = this.filterTagsArray.length;
      for (var i=0; i<len; i++) {
        this.filterTagsArray.pop();
      }
      if (val.length == 0) return;
      val.split(",").map(item => {
        this.filterTagsArray.push(item.trim());
      });
    },
    filterStatus(val) {
      const len = this.filterStatusArray.length;
      for (var i=0; i<len; i++) {
        this.filterStatusArray.pop();
      }
      if (val.length == 0) return;
      val.split(",").map(item => this.filterStatusArray.push(item.trim()));
    }
  },
  computed: {

  },
  methods: {
    clearAll() {
      this.filterTags = '';
      this.filterStatus = '';
    },
    applyFilter() {
      
    }
  }
}
</script>