<template>
  <v-form @submit="onSubmit" class="">
    <v-text-field type="text" name="username" placeholder="Username" v-model="username"/>
    <v-text-field type="password" name="password" placeholder="Password" v-model="password" />
    <v-btn type="submit">Login</v-btn>
  </v-form>
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