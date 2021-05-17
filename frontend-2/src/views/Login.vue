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
      users: [],
    };
  },
  created() {
    if (localStorage.data) {
      this.$router.push({name: 'Projects'})
    }
  },
  methods: {
    async onSubmit(user) {
      this.users = await fetch(`${process.env.VUE_APP_BACKEND_URL}/api/users`);
      const data = await this.users.json();
      const userInDB = data.find(
        (u) => u.username == user.username && u.password == user.password
      );
      if (userInDB != null) {
        localStorage.setItem('data', JSON.stringify(userInDB));
        this.$store.dispatch("fetchCurrentUser");
        this.$router.push({name: 'Projects'})
      }
    },
  },
};
</script>

<style>
</style>