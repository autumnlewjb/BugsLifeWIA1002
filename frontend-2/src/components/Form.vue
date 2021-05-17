<template>
  <v-form @submit="onSubmit" class="">
    <v-text-field type="text" name="username" placeholder="Username" v-model="username"/>
    <v-text-field type="email" name="email" placeholder="Email" v-model="email" v-if="collectEmail" />
    <v-text-field type="password" name="password" placeholder="Password" v-model="password"/>
    <v-btn type="submit">{{formPurpose}}</v-btn>
  </v-form>
</template>

<script>
export default {
  name: "Form",
  data() {
    return {
      username: '',
      password: '',
      email: ''
    }
  },
  props: {
    collectEmail: Boolean,
    formPurpose: String
  },
  setup() {},
  methods: {
    onSubmit(e) {
      e.preventDefault()
      const user = {
        username: this.username,
        password: this.password,
        email: this.email
      }

      this.$emit('form-submit', user)
    }
  },
};
</script>

<style scoped>
</style>