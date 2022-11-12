import Vue from 'vue'
import VueCompositionAPI from '@vue/composition-api'
import {Fragment} from 'vue-frag'
import {Chart} from "chart.js"
import App from '@/App'
import router from '@/router'
import store from '@/store'
import axios from '@/axios'
import VueAxios from 'vue-axios'
import VueScrollTo from 'vue-scrollto'
import _ from 'lodash'
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import '@/assets/styles/style.sass'

Vue.config.productionTip = false

Chart.defaults.font.family = "'Trebuchet MS', Helvetica, Arial, sans-serif"

Vue.use(VueCompositionAPI)
Vue.use(VueAxios, axios)
Vue.use(VueScrollTo)
Vue.use(BootstrapVue)
Vue.use(BootstrapVueIcons)

Vue.prototype.$_ = _

Vue.component('fragment', Fragment)

if (process.env.NODE_ENV === 'development') {
  import('vue-lorem-ipsum').then(({ LoremIpsum }) => {
    Vue.component('lorem-ipsum', LoremIpsum)
  })
}

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
