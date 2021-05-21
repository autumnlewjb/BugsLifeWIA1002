import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    currentUser: JSON.parse(localStorage.getItem('data')),
    username: '',
    userId: '',
    email: '',
    projects: [],
  },
  mutations: {
    setCurrentUser(state, payload) {
      state.currentUser = payload;
      if (payload) {
        state.username = payload.username;
        state.userId = payload.user_id;
        state.email = payload.email;
        state.projects = payload.project;
      }
    } 
  },
  actions: {
    async fetchCurrentUser(state) {
      if (localStorage.data) {
        const currentUser = JSON.parse(localStorage.data);
        fetch(`/api/user/${currentUser.username}`)
        .then((res) => {
          if (res.status == 200) {
            return res.json()
          }
        }).then((user) => {
          state.commit('setCurrentUser', user)
          localStorage.setItem('data', JSON.stringify(user))
        })
      } else {
        state.commit('setCurrentUser', null)
      }
    }
  },
  modules: {
  },
  getters: {
    getCurrentUser(state) {
      return state.currentUser;
    },
    getUsername(state) {
      return state.username;
    },
    getUserId(state) {
      return state.userId;
    },
    getEmail(state) {
      return state.email;
    },
    getProjects(state) {
      return state.projects;
    }
  }
})
