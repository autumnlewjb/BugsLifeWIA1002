import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const store = new Vuex.Store({
  state: {
    currentUser: JSON.parse(localStorage.getItem('data')),
    username: '',
    userId: '',
    email: '',
  },
  mutations: {
    setCurrentUser(state, payload) {
      state.currentUser = payload;
      if (payload) {
        state.username = payload.username;
        state.userId = payload.user_id;
        state.email = payload.email;
      }
    }
  },
  actions: {
    async fetchCurrentUser(state) {
      if (localStorage.data) {
        const currentUser = JSON.parse(localStorage.data);
        state.commit('setCurrentUser', currentUser)
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
});

export default store;
