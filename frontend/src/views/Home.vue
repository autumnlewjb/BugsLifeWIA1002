<template>
  <v-container class="title" fill-height align-center v-if="!loginPage" :key="loginPage">
    <v-container>
      <v-row class="ma-0">
        <v-col cols="12">
          <v-slide-x-transition>
            <v-container class="mb-5" v-if="fadeTransition">
              <p class="main-title">Bugs Life</p>
            </v-container>
          </v-slide-x-transition>
          <transition name="fade" appear>
            <v-container v-if="slideTransition">
              <p class="subtitle">Just another tracker.</p>
            </v-container>
          </transition>
          <v-container>
            <v-slide-x-reverse-transition>
              <div v-if="buttonTransition">
                <v-btn text x-large @click="clickHandler" plain>Start another boring day</v-btn>
              </div>
              <!-- <div v-if="loginRegisterTransition">
                <v-btn text x-large :to="{name: 'Login'}" >Login</v-btn>
                <v-btn text x-large :to="{name: 'Register'}">Register</v-btn>
              </div> -->
            </v-slide-x-reverse-transition>
          </v-container>
        </v-col>
      </v-row>
    </v-container>
  </v-container>
  <v-row v-else-if="loginPage" class="title justify-space-around align-center fill-height">
    <transition name="slide" appear>
      <v-col cols="6">
        <v-row>
          <v-col cols="12" class="d-flex justify-center mb-5 mt-1">
            <p class="main-title">Bugs Life</p>
          </v-col>
          <v-col class="d-flex justify-center">
            <p class="subtitle">Just another tracker.</p>
          </v-col>
          <v-container>
            <div>
              <v-btn text x-large @click="clickHandler" plain>Back to homepage</v-btn>
            </div>
          </v-container>
        </v-row>
      </v-col>
    </transition>
    <transition name="fade" appear>
      <v-divider class="ma-2" vertical></v-divider>
    </transition>
    <transition name="fade" appear>
      <v-col cols="4">
        <Login/>
      </v-col>
    </transition>
  </v-row>
</template>

<script>

import Login from "./Login";

export default {
  name: 'Home',
  components: {Login},
  data() {
    return {
      fadeTransition: false,
      slideTransition: false,
      buttonTransition: false,
      loginRegisterTransition: false,
      loginPage: false
    }
  },
  mounted() {
    this.fadeTransition = true;
    this.slideTransition = true;
    this.buttonTransition = true;
  },
  methods: {
    clickHandler() {
      this.loginPage = !this.loginPage
      //this.loginRegisterTransition = !this.loginRegisterTransition;
      // this.buttonTransition = !this.buttonTransition;
    }
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Roboto+Mono:wght@500&family=Staatliches&display=swap');

.title {
  text-align: center;
}

.main-title {
  font-family: 'Staatliches', cursive;
  font-size: 10rem;
}

.subtitle {
  font-family: 'Roboto Mono', monospace;
  font-size: 2.5rem;
}

.fade-enter-active {
  transition-delay: .3s;
  transition: opacity 3s;
}

.fade-leave-active {
  transition: opacity .1s;
}

.fade-enter, .fade-leave-to /* .fade-leave-active below version 2.1.8 */
{
  opacity: 0;
  transition-delay: 5s;
}

.slide-enter-active {
  transition: all 0.5s ease-out;
}

.slide-enter {
  transform: translateX(40%);
}

</style>
