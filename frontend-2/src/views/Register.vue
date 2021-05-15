<template>
<div>
  <h1>Register</h1>
  <Form @form-submit="onSubmit"></Form>
</div>
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
      users: this.data["users"]
    }
  },
  methods: {
    onSubmit(user) {
      const exist = this.users.find(
        (userInDB) =>
          userInDB.username == user.username &&
          userInDB.password == userInDB.password
      );
      if (exist == null) {
        console.log("registered");
        router.push({"name": "Login"})
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