<template>
  <v-row class="justify-center align-center">
    <v-col cols="12">
      <v-form @submit="onSubmit">
        <v-text-field type="text" name="username" v-model="username" outlined color="teal" label="Username"/>
        <v-text-field type="email" name="email" v-model="email" v-if="collectEmail" outlined color="teal"
                      label="Email"/>
        <v-text-field :type="hide ? 'text' : 'password'" name="password" v-model="password" outlined
                      color="teal" :append-icon="hide ? 'mdi-eye' : 'mdi-eye-off'" @click:append="hide = !hide"
                      label="Password"/>
      </v-form>
    </v-col>
    <v-col cols="8" class="d-flex child-flex justify-center">
      <v-btn type="submit" color="teal" dark @click="onSubmit">{{ formPurpose }}</v-btn>
    </v-col>
  </v-row>
</template>

<script>
export default {
  name: "Form",
  data() {
    return {
      username: '',
      password: '',
      email: '',
      hide: false
    }
  },
  props: {
    collectEmail: Boolean,
    formPurpose: String
  },
  setup() {
  },
  methods: {
    onSubmit(e) {
      e.preventDefault()
      const user = {
        username: this.username,
        password: this.password,
        email: this.email
      }

      this.username = '';
      this.password = '';
      this.email = '';

      this.$emit('form-submit', user)
    }
  },
};
</script>

<style scoped>
</style>