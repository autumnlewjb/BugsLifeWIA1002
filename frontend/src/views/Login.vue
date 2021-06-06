<template>
  <v-container>
    <v-layout row justify-center>
      <v-flex xs12 md11 class="d-flex justify-center mb-2">
        <h1>Login</h1>
      </v-flex>
    </v-layout>
    <v-layout row justify-center>
      <v-flex xs12 md6>
        <v-card class="d-flex justify-center pa-5 mx-2">
          <Form @form-submit="onSubmit" formPurpose="Login" :collectEmail="false"></Form>
        </v-card>
      </v-flex>
    </v-layout>
    <v-dialog v-model="loginFailed" width="500">
      <v-card>
        <v-card-title class="headline grey lighten-2">Login Failed</v-card-title>
        <v-card-text class="my-2">You might have entered the wrong username or password. <br>Please try again later. </v-card-text>
        <v-card-actions class="d-flex justify-end">
          <v-btn @click="loginFailed = false" text>
            Dismiss
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import Form from "../components/Form";

export default {
  name: "Login",
  components: {
    Form,
  },
  props: {
    data: Object,
  },
  data() {
    return {
      loginFailed: false
    };
  },
  created() {
    if (this.$store.getters.getCurrentUser) {
      this.$router.push({name: 'Profile'})
    }
  },
  methods: {
    async onSubmit(user) {
      fetch(`/api/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
      })
      .then((res) => {
        if (res.status == 200) {
          return res.json()
        } else if (res.status == 401) {
          this.loginFailed = true;
        } else {
          return null
        }
      })
      .then((data) => {
        if (data) {
          console.log(data)
          localStorage.setItem('data', JSON.stringify(data))
          this.$store.dispatch('fetchCurrentUser')
          this.$router.push({name: 'Profile'})
        }
      })
      .catch((e) => console.log(e))
    },
  },
};
</script>

<style>
</style>