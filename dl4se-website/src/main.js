import Vue from "vue";
import { Fragment } from "vue-frag";
import { Chart } from "chart.js";
import AOS from "aos";
import App from "@/App";
import router from "@/router";
import store from "@/store";
import axios from "@/axios";
import unhead from "@/unhead";
import { VAos } from "@/directives";
import VueAxios from "vue-axios";
import VueScreen from "vue-screen";
import VueScrollTo from "vue-scrollto";
import { UnheadPlugin as VueUnhead } from "@unhead/vue/vue2";
import { BootstrapVue, BootstrapVueIcons } from "bootstrap-vue";
import "aos/dist/aos.css";
import "@/assets/styles/style.sass";
import "@/sw";

Vue.config.productionTip = false;

Chart.defaults.font.family = "'Trebuchet MS', Helvetica, Arial, sans-serif";

Vue.use(VueUnhead);
Vue.use(VueAxios, axios);
Vue.use(VueScreen, "bootstrap");
Vue.use(VueScrollTo);
Vue.use(BootstrapVue);
Vue.use(BootstrapVueIcons);

Vue.component("fragment", Fragment);

Vue.prototype.$AOS = AOS;
Vue.directive("aos", VAos);

new Vue({
  router,
  store,
  unhead,
  render: (h) => h(App),
  mounted() {
    this.$AOS.init();
  },
}).$mount("#app");
