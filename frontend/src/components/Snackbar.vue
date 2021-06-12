<template>
  <v-container>
    <v-snackbar v-model="snackbar">
      {{ text }}
      <template v-slot:action="{ attrs }">
        <v-btn color="pink" text v-bind="attrs" @click="closeSnackbar">
          Close
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
export default {
  name: "Snackbar",
  props: {
    snackbar: Boolean,
    text: String
  },
  watch: {
    snackbar() {
      setTimeout(() => this.closeSnackbar(), 2000);
    }
  },
  data() {
    return {
      timeout: 2000,
    }
  },
  methods: {
    closeSnackbar() {
      this.$emit("close-snackbar")
    }
  }
}
</script>

<style scoped>

</style>