<template>
  <v-row class="justify-space-around align-center fill-height title">
    <transition name="slide" appear>
      <v-col cols="6" class="pb-1">
        <v-row>
          <v-col cols="12" class="d-flex justify-center mb-5">
            <p class="main-title">Bugs Life</p>
          </v-col>
          <v-col cols="12" class="d-flex justify-center">
            <p class="subtitle">Just another tracker.</p>
          </v-col>
          <v-col class="d-flex justify-center">
            <v-btn text x-large plain :to="{name:'Home'}">Back to homepage</v-btn>
          </v-col>
        </v-row>
      </v-col>
    </transition>
    <transition name="fade" appear>
      <v-divider class="ma-2" vertical></v-divider>
    </transition>
    <transition name="fade" appear>
      <v-col cols="4">
        <v-col cols="12" class="d-flex justify-center">
          <h1>Welcome back!</h1>
        </v-col>
        <v-col cols="12" class="d-flex justify-center">
          <Form @form-submit="onSubmit" formPurpose="Login" :collectEmail="false" :loading="loading"
                :register="false"></Form>
        </v-col>
      </v-col>
    </transition>
    <v-dialog v-model="loginFailed" width="500">
      <v-card>
        <v-card-title class="headline grey lighten-2">Login Failed</v-card-title>
        <v-card-text class="my-2">You might have entered the wrong username or password. <br>Please try again later.
        </v-card-text>
        <v-card-actions class="d-flex justify-end">
          <v-btn @click="loginFailed = false" text>
            Dismiss
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import Form from "../components/Form";

export default {
  name: "Login",
  components: {
    Form,
  },
  props: {
    data: Object,
  },
  data() {
    return {
      loginFailed: false,
      fadeTransition: false,
      loading: false
    };
  },
  created() {
    if (this.$store.getters.getCurrentUser) {
      this.$router.push({name: 'Profile'})
    }
  },
  methods: {
    async onSubmit(user) {
      this.loading = true;
      fetch(`/api/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
      })
          .then((res) => {
            if (res.status == 200) {
              return res.json()
            } else if (res.status == 401) {
              this.$store.commit('setSessionExpired', false);
              this.loginFailed = true;
            } else {
              return null;
            }
          })
          .then((data) => {
            if (data) {
              
              localStorage.setItem('data', JSON.stringify(data));
              this.$store.commit('setCurrentUser', data);
              this.$router.push({name: 'Profile'})
            }
          })
          .catch((e) => console.log(e))
          .finally(() => this.loading = false);
    },
  },
  mounted() {
    this.fadeTransition = true;
  }
};
</script>

<style>
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

.slide-leave-active {
  transition: all 0.5s ease-out;
}

.slide-leave {
  transform: translateX(-40%);
}
</style>