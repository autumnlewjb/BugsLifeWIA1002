<template>
  <v-row class="justify-space-around align-center fill-height">
    <v-col cols="12" class="d-flex justify-center">
      <h1>Welcome back!</h1>
    </v-col>
    <v-col cols="12" class="d-flex justify-center">
      <Form @form-submit="onSubmit" formPurpose="Login" :collectEmail="false"></Form>
    </v-col>
    <v-dialog v-model="loginFailed" width="500">
      <v-card>
        <v-card-title class="headline grey lighten-2">Login Failed</v-card-title>
        <v-card-text class="my-2">You might have entered the wrong username or password. <br>Please try again later.
        </v-card-text>
        <v-card-actions class="d-flex justify-end">
          <v-btn @click="loginFailed = false" text>
            Dismiss
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
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
      loginFailed: false,
      fadeTransition: false
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
  mounted() {
    this.fadeTransition = true
  }
};
</script>

<style>
</style>