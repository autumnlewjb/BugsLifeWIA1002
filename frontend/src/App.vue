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
          <v-list-item v-for="item in items" :key="item.title" @click="item.click" link>
            <v-list-item-icon>
              <v-icon>{{ item.icon }}</v-icon>
            </v-list-item-icon>

            <v-list-item-content>
              <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </v-navigation-drawer>
    </nav>
    <v-main>
      <!-- <v-container>
        <v-breadcrumbs :items="getBreadcrumbsItem">

        </v-breadcrumbs>
      </v-container> -->
      <v-container :class="{'pa-16': $vuetify.breakpoint.mdAndUp, 'pa-5': $vuetify.breakpoint.smAndDown}">
        <router-view
          @addToBreadcrumb="addToBreadcrumb"
          @removeFromBreadcrumb="removeFromBreadcrumb"
        ></router-view>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
export default {
  name: "App",
  data() {
    return {
      data: null,
      drawer: true,
      items: [
        { title: "Projects", icon: "mdi-book", route:"Projects", click: this.goToProject },
        { title: "Logout", icon: "mdi-logout", route:"Login", click: this.logOut },
      ],
      mini: true,
      breadcrumbItems: []
    };
  },
  components: {},
  created() {
    if (localStorage.data) {
      this.data = JSON.parse(localStorage.data);
      this.drawer = true;
    } else {
      this.drawer = false;
    }
  },
  destroyed() {
  },
  methods: {
    goToProject() {
      this.$router.push({name: 'Projects'}).catch(() => {})
    },
    logOut() {
      fetch(`/api/logout`).then((res) => {
        if (res.status == 200) {
          localStorage.clear()
          this.breadcrumbItems = []
          this.$store.dispatch('fetchCurrentUser')
          this.$router.push({name: 'Login'})
        } else {
          console.log("logout failed")
        }
      })
    },
    addToBreadcrumb(breadcrumbItem) {
      if (this.breadcrumbItems.length == 0 || this.breadcrumbItems[this.breadcrumbItems.length - 1].href !== breadcrumbItem.href) {
        this.breadcrumbItems.push(breadcrumbItem)
      }
      this.breadcrumbItems.map((item) => item.disabled = false)
      this.breadcrumbItems[this.breadcrumbItems.length-1].disabled = true
    },
    removeFromBreadcrumb() {
      if (this.breadcrumbItems.length > 0) this.breadcrumbItems.pop()
    }
  },
  computed: {
    userAuthenticated() {
      return this.$store.getters.getCurrentUser != null
    },
    getBreadcrumbsItem() {
      return this.breadcrumbItems
    },
    getData() {
      return this.$store.getters.getCurrentUser;
    }
  },
};
</script>
