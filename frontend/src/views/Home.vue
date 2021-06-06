<template>
  <v-container class="title" fill-height align-center>
    <v-container>
      <v-row class="ma-0">
        <v-col cols="12">
          <v-slide-x-transition>
            <v-container class="mb-5" v-if="fadeTransition">
              <p class="main-title">Bugs Life</p>
            </v-container>
          </v-slide-x-transition>
          <transition name="fade">
            <v-container v-if="slideTransition">
              <p class="subtitle">Just another tracker.</p>
            </v-container>
          </transition>
          <v-container>
            <v-slide-x-reverse-transition>
              <div v-if="buttonTransition">
                <v-btn text x-large @click="clickHandler" :to="{name: 'Login'}" plain>Start another boring day</v-btn>
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
</template>

<script>
  export default {
    name: 'Home',
    data() {
      return {
        fadeTransition: false,
        slideTransition: false,
        buttonTransition: false,
        loginRegisterTransition: false
      }
    },
    mounted() {
      this.fadeTransition = true;
      this.slideTransition = true;
      this.buttonTransition = true;
    },
    methods: {
      clickHandler() {
        // this.buttonTransition = !this.buttonTransition;
        // this.loginRegisterTransition = !this.loginRegisterTransition;
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
.fade-enter, .fade-leave-to /* .fade-leave-active below version 2.1.8 */ {
  opacity: 0;
  transition-delay: 5;
}

</style>
