import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    currentUser: JSON.parse(localStorage.getItem('data'))
  },
  mutations: {
    setCurrentUser(state, payload) {
      console.log("set user")
      state.currentUser = payload;
    } 
  },
  actions: {
    async fetchCurrentUser(state) {
      if (localStorage.data) {
        const currentUser = JSON.parse(localStorage.data);
        fetch(`${process.env.VUE_APP_BACKEND_URL}/api/user/${currentUser.username}`)
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
  }
})
