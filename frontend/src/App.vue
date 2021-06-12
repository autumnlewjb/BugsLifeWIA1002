<template>
  <v-app>
    <nav>

      <v-navigation-drawer
        v-if="userAuthenticated"
        app
        v-model="drawer"
        :mini-variant.sync="mini"
        permanent
      >
        <v-list-item class="px-2">
          <v-list-item-avatar color="blue">
            <span class="white--text headline">{{(getData.username[0].toUpperCase())}}</span>
          </v-list-item-avatar>

          <v-list-item-title>{{(getData.username)}}</v-list-item-title>

          <v-btn icon @click.stop="mini = !mini">
            <v-icon>mdi-chevron-left</v-icon>
          </v-btn>
        </v-list-item>

        <v-divider></v-divider>

        <v-list dense>
          <v-list-item v-for="(item, index) in items" :key="index" link :to="{name: item.route}">
            <v-list-item-icon>
              <v-icon>{{ item.icon }}</v-icon>
            </v-list-item-icon>

            <v-list-item-content>
              <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item @click="logOut">
            <v-list-item-icon>
              <v-icon>mdi-logout</v-icon>
            </v-list-item-icon>

            <v-list-item-content>
              <v-list-item-title>Logout</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </v-navigation-drawer>
    </nav>
    <v-main>
      <v-container v-if="$route.name != 'Search' && userAuthenticated">
        <v-layout>
          <v-flex sm0 md8></v-flex>
          <v-flex>
            <v-combobox
              placeholder="Search for projects, issues and more"
              filled
              rounded
              prepend-icon="mdi-magnify"
              append-icon=""
              @focus="$router.push({name: 'Search'})"
              dense
            ></v-combobox>
          </v-flex>
        </v-layout>
        
      </v-container>
        <router-view :class="{'pa-10': $vuetify.breakpoint.mdAndUp, 'pa-3': $vuetify.breakpoint.smAndDown}"
        ></router-view>
      <Snackbar :snackbar="showSnackbar" :text="getMessage" @close-snackbar="handleCloseSnackbar"/>
      <v-dialog v-model="getSessionExpired" persistent width="300">
          <v-card>
              <v-card-title>Session Expired</v-card-title>
              <v-card-text>Your session was ended. Please log in again. </v-card-text>
              <v-card-actions>
                  <v-btn plain @click="$store.commit('setSessionExpired', false)">
                      Dismiss
                  </v-btn>
              </v-card-actions>
          </v-card>
      </v-dialog>
    </v-main>
  </v-app>
</template>

<script>
import Snackbar from './components/Snackbar';
import './fetch'
export default {
  name: "App",
  data() {
    return {
      data: null,
      drawer: true,
      items: [
        { title: "Profile", icon: "mdi-human-greeting", route:"Profile" },
        { title: "Projects", icon: "mdi-book", route:"Projects" },
        { title: "Search", icon: "mdi-magnify", route: "Search" },
      ],
      mini: true,
      showProfile: false
    };
  },
  components: {
    Snackbar
  },
  created() {
    console.log("created");
    // this.$store.dispatch('fetchCurrentUser');
  },
  watch: {
    showSnackbar(val) {
      if (!val) {
        this.$store.commit('resetSuccessEdit');
      }
    }
  },
  methods: {
    goToProject() {
      this.$router.push({name: 'Projects'}).catch(() => {})
    },
    logOut() {
      this.$store.dispatch('logout').then((res) => {
        console.log(res);
        this.$router.push({name: 'Home'});
      });
    },
    goToSearch() {
      this.$router.push({name: 'Search'}).catch(() => {})
    },
    handleCloseSnackbar() {
      this.$store.commit('resetSuccessEdit');
    }
  },
  computed: {
    userAuthenticated() {
      return this.$store.getters.getCurrentUser != null
    },
    getData() {
      return this.$store.getters.getCurrentUser;
    },
    getMessage() {
      return this.$store.getters.getEditSubject + " edited";
    },
    showSnackbar() {
      return this.$store.getters.getSuccessEdit;
    },
    getSessionExpired() {
      return this.$store.getters.getSessionExpired;
    }
  },
};
</script>
