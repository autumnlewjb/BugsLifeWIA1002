<template>
  <v-app>
    <nav>

      <v-navigation-drawer
        v-if="makeNavVisible"
        app
        v-model="drawer"
        :mini-variant.sync="mini"
        permanent
      >
        <v-list-item class="px-2">
          <v-list-item-avatar color="blue">
            <span class="white--text headline">{{(this.data.username[0].toUpperCase())}}</span>
          </v-list-item-avatar>

          <v-list-item-title>{{(this.data.username)}}</v-list-item-title>

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
      <div :class="{'pa-16': $vuetify.breakpoint.mdAndUp, 'pa-5': $vuetify.breakpoint.smAndDown}">
        <router-view
          @updateData="updateUserData"
          :data="data"
        ></router-view>
      </div>
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
    };
  },
  components: {},
  created() {
    if (localStorage.data) {
      this.data = JSON.parse(localStorage.data);
      console.log(this.data);
      this.drawer = true;
    } else {
      this.drawer = false;
    }
  },
  methods: {
    updateUserData() {
      this.data = JSON.parse(localStorage.data);
      console.log(this.data);
      this.$router.push({ name: "Projects" });
    },
    goToProject() {
      this.$router.push({name: 'Projects'}).catch(() => {})
    },
    logOut() {
      localStorage.clear()
      this.data = null
      this.$router.push({name: 'Login'})
    }
  },
  computed: {
    makeNavVisible() {
      return this.data != null
    },
  },
  emits: ["updateData"],
};
</script>
