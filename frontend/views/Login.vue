<template>
  <h1>Login</h1>
  <Form @form-submit="onSubmit"></Form>
</template>

<script>
import Form from '../src/components/Form'
import router from '../router/index'

export default {
    name: 'Login',
    components: {
        Form
    },
    props: {
      data: JSON
    },
    data() {
      return {
        users: this.data["users"]
      }
    },
    methods: {
      onSubmit(user) {
        const exist = this.users.find((userInDB) => userInDB.username == user.username && userInDB.password == userInDB.password)
        if (exist != null) {
          router.push({'name': 'Projects'})
        }
      }
    }
};
</script>

<style>
</style>