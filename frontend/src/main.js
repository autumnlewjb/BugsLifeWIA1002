import Vue from 'vue'
import router from './router/index'
import App from './App'
import vuetify from './plugins/vuetify'
import store from './store'
import HighchartsVue from 'highcharts-vue'

Vue.config.productionTip = false

new Vue({
  router: router,
  vuetify,
  store,
  HighchartsVue,
  render: h => h(App)
}).$mount('#app')
