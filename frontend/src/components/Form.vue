<template>
  <form @submit="onSubmit">
    <input type="text" name="username" placeholder="Username" v-model="username"/>
    <input type="password" name="password" placeholder="Password" v-model="password" />
    <input type="submit" />
  </form>
</template>

<script>
export default {
  name: "Form",
  data() {
    return {
      username: '',
      password: '',
    }
  },
  props: {},
  setup() {},
  methods: {
    onSubmit(e) {
      e.preventDefault()
      const user = {
        username: this.username,
        password: this.password,
      }

      this.$emit('form-submit', user)
    }
  },
};
</script>

<style scoped>
</style>