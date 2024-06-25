import Vue from "vue";
import { Fragment } from "vue-frag";
import { Chart } from "chart.js";
import App from "@/App";
import router from "@/router";
import store from "@/store";
import axios from "@/axios";
import unhead from "@/unhead";
import VueAxios from "vue-axios";
import VueScreen from "vue-screen";
import VueScrollTo from "vue-scrollto";
import VueHighlight from "@highlightjs/vue-plugin";
import { UnheadPlugin as VueUnhead } from "@unhead/vue/vue2";
import { BootstrapVue, BootstrapVueIcons } from "bootstrap-vue";
import "@/assets/styles/style.sass";
import "@/sw";
import "@/hljs";

Vue.config.productionTip = false;

Chart.defaults.font.family = "'Trebuchet MS', Helvetica, Arial, sans-serif";

Vue.use(VueUnhead);
Vue.use(VueAxios, axios);
Vue.use(VueScreen, "bootstrap");
Vue.use(VueScrollTo);
Vue.use(VueHighlight);
Vue.use(BootstrapVue);
Vue.use(BootstrapVueIcons);

Vue.component("fragment", Fragment);

new Vue({
  router,
  store,
  unhead,
  render: (h) => h(App),
}).$mount("#app");
