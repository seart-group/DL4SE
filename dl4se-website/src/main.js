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
Vue.directive("aos", (el, binding) => {
  const config = binding.value || {};
  const modifiers = binding.modifiers || {};
  Object.entries({
    "data-aos": config.animation,
    "data-aos-offset": `${config.offset ?? 120}`,
    "data-aos-delay": `${config.delay ?? 0}`,
    "data-aos-duration": `${config.duration ?? 400}`,
    "data-aos-easing": config.easing || "ease",
    "data-aos-anchor-placement": config.anchorPlacement || "top-bottom",
    "data-aos-once": `${!!modifiers.once}`,
    "data-aos-mirror": `${!!modifiers.mirror}`,
  }).forEach(([key, value]) => el.setAttribute(key, value));
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
