import Vue from 'vue'
import router from './router/index'
import App from './App'
import vuetify from './plugins/vuetify'

Vue.config.productionTip = false

new Vue({
  router: router,
  vuetify,
  render: h => h(App)
}).$mount('#app')
