<template>
    <v-container>
      <v-row>
        <v-col>
          <v-container>
            <v-row v-for="(item, index) in sortData" :key="index" justify="center">
              <v-col sm="6" md="6">
                <v-select :items="sortSubjects" solo label="sort by" v-model="item.subject" disabled></v-select>
              </v-col>
              <v-col sm="4" md="4">
                <v-radio-group row v-model="item.order" disabled>
                  <v-radio label="Ascending" value="asc"></v-radio>
                  <v-radio label="Descending" value="desc"></v-radio>
                </v-radio-group>
              </v-col>
            </v-row>
          </v-container>
          <v-form>
            <v-row v-if="availableSortSubjects.length > 0" justify="center">
              <v-col sm="6" md="6">
                <v-select :items="availableSortSubjects" solo label="sort by" v-model="sortSubject"></v-select>
              </v-col>
              <v-col sm="4" md="4">
                <v-radio-group row v-model="sortOrder">
                  <v-radio label="Ascending" value="asc"></v-radio>
                  <v-radio label="Descending" value="desc"></v-radio>
                </v-radio-group>
              </v-col>
              <v-col sm="2" md="2" v-if="availableSortSubjects.length > 1">
                <v-btn text icon @click="addNewSort">
                  <v-icon>mdi-plus</v-icon>
                </v-btn>
              </v-col>
            </v-row>
            <v-row class="d-flex justify-end">
              <v-btn text @click="applySort" v-if="availableSortSubjects.length > 0">Apply</v-btn>
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
    sortSubjects: Array,
    alreadyInSort: Array,
  },
  data() {
    return {
      sortData: [],
      sortSubject: "",
      sortOrder: "",
      availableSortSubjects: []
    }
  },
  created() {
    this.sortData = this.alreadyInSort;
    this.availableSortSubjects = this.sortSubjects;
  },
  watch: {
    sortData(val) {
      this.availableSortSubjects = this.sortSubjects.filter((item) => val.filter((data) => data.subject == item.value).length == 0);
      console.log(this.availableSortSubjects);
      console.log("-");
      console.log(val);
    }
  },
  methods: {
    addNewSort() {
      if (this.sortSubject == '' || this.sortOrder == '') return;
      if (this.sortSubject == null) {
        console.log("not found");
        return;
      }
      this.sortData.push({
        subject: this.sortSubject,
        order: this.sortOrder
      });
      this.sortSubject = "";
      this.sortOrder = "";
      console.log(this.sortData);
    },
    clearAll() {
      const len = this.sortData.length;
      for (var i=0; i<len; i++) {
        this.sortData.pop();
      }
      this.sortSubject = '';
      this.sortOrder = '';
      // this.$emit('acceptSortQuery', this.sortData)
    },
    applySort() {
      if (this.sortSubject != '' && this.sortOrder != '') {
        this.addNewSort();
      }
      // this.$emit('acceptSortQuery', this.sortData);
    }
  }
}
</script>