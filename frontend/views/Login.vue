<template>
  <h1>Login</h1>
  <Form @form-submit="onSubmit"></Form>
</template>

<script>
import Form from '../src/components/Form'

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
        users: []
      }
    },
    methods: {
      async onSubmit(user) {
        this.users = await fetch('/api/users')
        const data = await this.users.json()
        const userInDB = data.find((u) => u.username == user.username && u.password == user.password)
        if (userInDB != null) {
          this.$emit('updateData', userInDB)
        }
      }
    },
    emits: ['updateData']
};
</script>

<style>
</style>