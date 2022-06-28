import Vue from 'vue'
import VueCompositionAPI from '@vue/composition-api'
import {Plugin as VueFragment} from 'vue-fragment'
import App from '@/App'
import router from '@/router'
import store from '@/store'
import axios from 'axios'
import VueAxios from 'vue-axios'
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import '@/assets/styles/style.sass'

Vue.config.productionTip = false

Vue.use(VueCompositionAPI)
Vue.use(VueFragment)
Vue.use(VueAxios, axios)
Vue.use(BootstrapVue)
Vue.use(BootstrapVueIcons)

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
