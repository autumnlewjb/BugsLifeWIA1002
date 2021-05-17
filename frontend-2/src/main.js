import Vue from 'vue'
import router from './router/index'
import App from './App'
import vuetify from './plugins/vuetify'
import store from './store'

Vue.config.productionTip = false

new Vue({
  router: router,
  vuetify,
  store,
  render: h => h(App)
}).$mount('#app')
