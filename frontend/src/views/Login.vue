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
    };
  },
  created() {
    if (this.$store.getters.getCurrentUser) {
      this.$router.push({name: 'Projects'})
    }
  },
  methods: {
    async onSubmit(user) {
      fetch(`/api/authenticate`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
      })
      .then((res) => {
        if (res.status == 200) {
          return res.json()
        } else {
          return null
        }
      })
      .then((data) => {
        if (data && data.user) {
          const user = JSON.parse(data.user)
          console.log(user)
          localStorage.setItem('data', JSON.stringify(user))
          this.$store.dispatch('fetchCurrentUser')
          this.$router.push({name: 'Projects'})
        }
      })
      .catch((e) => console.log(e))
    },
  },
};
</script>

<style>
</style>