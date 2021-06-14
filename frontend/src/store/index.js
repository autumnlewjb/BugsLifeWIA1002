import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const store = new Vuex.Store({
  state: {
    currentUser: JSON.parse(localStorage.getItem('data')),
    successEdit: false,
    editSubject: '',
    sessionExpired: false
  },
  mutations: {
    setCurrentUser(state, payload) {
      state.currentUser = payload;
    },
    clearUserState(state) {
      state.currentUser = null;
    },
    setSuccessEdit(state, payload) {
      state.successEdit = true;
      state.editSubject = payload;
    },
    resetSuccessEdit(state) {
      state.successEdit = false;
    },
    setSessionExpired(state, payload) {
      state.sessionExpired = payload;
    }
  },
  actions: {
     fetchCurrentUser(state) {
      return new Promise((resolve, reject) => {
        fetch(`/api/user`)
        .then(res => {
          if (res.status == 200) {
            return res.json();
          } else {
            return null;
          }
        })
        .then(data => {
          if (data) {
            delete data.password
            state.commit('setCurrentUser', data);
            
            localStorage.setItem('data', JSON.stringify(data));
          }
          resolve(data);
        })
        .catch(e => reject(e))
      });
    },
    logout(state) {
      return new Promise((resolve, reject) => {
        fetch(`/api/logout`).then((res) => {
          if (res.status == 200) {
            localStorage.removeItem('data');
            state.commit('clearUserState');
            resolve(res);
          }
        })
        .catch(e => reject(e));
      });
    }
  },
  modules: {
  },
  getters: {
    getCurrentUser(state) {
      return state.currentUser;
    },
    getEditSubject(state) {
      return state.editSubject;
    },
    getSuccessEdit(state) {
      return state.successEdit;
    },
    getSessionExpired(state) {
      return state.sessionExpired;
    }
  }
});

export default store;
