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
          <Form @form-submit="onSubmit" formPurpose="Register" :collectEmail="true"></Form>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import Form from "../components/Form";
import router from "../router/index"
export default {
  setup() {},
  components: {
    Form,
  },
  data() {
    return {
      users: this.data == null ? [] : this.data["users"],
    }
  },
  methods: {
    async onSubmit(user) {
      const exist = this.users.find(
        (userInDB) =>
          userInDB.username == user.username &&
          userInDB.password == userInDB.password
      );
      if (exist == null) {
        const res = await fetch("/api/user/create", {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(user)
        })

        if (res.status === 200) {
          console.log("registered");
          router.push({"name": "Login"})
        } else {
          alert("Registration failed! ")
        }

      }
    },
  },
  props: {
    data: Object
  }
};
</script>

<style scoped>
</style>