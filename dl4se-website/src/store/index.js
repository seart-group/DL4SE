import Vue from "vue";
import Vuex from "vuex";
import VuexPersistence from "vuex-persist";
import router from "@/router";

Vue.use(Vuex);

const vuexLocal = new VuexPersistence({
  storage: window.localStorage,
});

export default new Vuex.Store({
  state: {
    token: null,
  },
  getters: {
    getToken(state) {
      return state.token;
    },
  },
  mutations: {
    setToken(state, value) {
      state.token = value;
    },
    clearToken(state) {
      state.token = null;
    },
  },
  actions: {
    async logOut(context, target) {
      context.commit("clearToken");
      const isPrivate = !router.currentRoute.meta.public;
      if (isPrivate) await router.replace({ name: "login", query: { target: target } });
      // else
      //   router.go(0)
      // Uncomment previous lines to enable refresh on public page logout
    },
  },
  modules: {},
  plugins: [vuexLocal.plugin],
});
