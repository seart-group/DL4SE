import Vue from 'vue'
import VueCompositionAPI from '@vue/composition-api'
import {Fragment} from 'vue-frag'
import {Chart} from "chart.js"
import AOS from 'aos'
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
import 'aos/dist/aos.css'
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

Vue.directive("aos", (el, binding) => {
  const config = binding.value
  const modifiers = binding.modifiers

  el.setAttribute("data-aos", config.animation)

  el.setAttribute("data-aos-offset", config.offset || "120")
  el.setAttribute("data-aos-delay", config.delay || "0")
  el.setAttribute("data-aos-duration", config.duration || "400")
  el.setAttribute("data-aos-easing", config.easing || "ease")
  el.setAttribute("data-aos-anchor-placement", config.easing || "top-bottom")

  el.setAttribute("data-aos-once", `${!!modifiers.once}`)
  el.setAttribute("data-aos-mirror", `${!!modifiers.mirror}`)
})

new Vue({
  router,
  store,
  render: h => h(App),
  mounted() {
    AOS.init()
  }
}).$mount('#app')
