import Vue from "vue";
import { Fragment } from "vue-frag";
import { Chart } from "chart.js";
import AOS from "aos";
import App from "@/App";
import router from "@/router";
import store from "@/store";
import axios from "@/axios";
import unhead from "@/unhead";
import VueAxios from "vue-axios";
import VueScreen from "vue-screen";
import VueScrollTo from "vue-scrollto";
import { UnheadPlugin as VueUnhead } from "@unhead/vue/vue2";
import _ from "lodash";
import { BootstrapVue, BootstrapVueIcons } from "bootstrap-vue";
import "aos/dist/aos.css";
import "@/assets/styles/style.sass";
import "@/registerServiceWorker";

Vue.config.productionTip = false;

Chart.defaults.font.family = "'Trebuchet MS', Helvetica, Arial, sans-serif";

Vue.use(VueUnhead);
Vue.use(VueAxios, axios);
Vue.use(VueScreen, "bootstrap");
Vue.use(VueScrollTo);
Vue.use(BootstrapVue);
Vue.use(BootstrapVueIcons);

Vue.prototype.$_ = _;

Vue.component("fragment", Fragment);

Vue.prototype.$AOS = AOS;
Vue.directive("aos", (el, binding) => {
  const config = binding.value;
  const modifiers = binding.modifiers;

  el.setAttribute("data-aos", config.animation);

  el.setAttribute("data-aos-offset", `${config.offset ?? 120}`);
  el.setAttribute("data-aos-delay", `${config.delay ?? 0}`);
  el.setAttribute("data-aos-duration", `${config.duration ?? 400}`);
  el.setAttribute("data-aos-easing", config.easing || "ease");
  el.setAttribute("data-aos-anchor-placement", config.easing || "top-bottom");

  el.setAttribute("data-aos-once", `${!!modifiers.once}`);
  el.setAttribute("data-aos-mirror", `${!!modifiers.mirror}`);
});

new Vue({
  router,
  store,
  unhead,
  render: (h) => h(App),
  mounted() {
    this.$AOS.init();
  },
}).$mount("#app");
