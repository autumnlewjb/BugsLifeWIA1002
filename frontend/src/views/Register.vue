<template>
  <v-container>
    <v-layout row justify-center>
      <v-flex xs12 md11 class="d-flex justify-center mb-2">
        <h1>Register</h1>
      </v-flex>
    </v-layout>
    <v-layout row justify-center>
      <v-flex xs12 md6>
        <v-card class="d-flex justify-center pa-5 mx-2">
          <Form @form-submit="onSubmit" formPurpose="Register" :collectEmail="true" :loading="loading"
                :register="true"></Form>
        </v-card>
      </v-flex>
    </v-layout>
    <Snackbar :snackbar="snackbar" :text="message" @close-snackbar="closeSnackbar"/>
  </v-container>
</template>

<script>
import Form from "../components/Form";
import Snackbar from "../components/Snackbar";
// import router from "../router/index"

export default {
  components: {
    Snackbar,
    Form,
  },
  data() {
    return {
      users: this.data == null ? [] : this.data["users"],
      snackbar: false,
      message: undefined,
      loading: false
    }
  },
  methods: {
    async onSubmit(user) {
      this.loading = true
      let url = undefined;
      if (user.role === 'User') {
        url = '/api/register';
      } else if (user.role === 'Admin') {
        url = '/api/register/admin';
      }
      const res = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(user)
      }).finally(() => this.loading = false)

      if (res.status === 200) {
        
        this.toggleSnackbar("Registered")
        // router.push({"name": "Login"});
      } else {
        this.toggleSnackbar("Registration failed! ")
        //alert("Registration failed! ");
      }
    },
    toggleSnackbar(text) {
      this.snackbar = true;
      this.message = text;
    },
    closeSnackbar() {
      this.snackbar = false;
      this.message = null;
    },
  },
  props: {
    data: Object
  }
};
</script>

<style scoped>
</style>