import Vue from 'vue'
import Vuex from 'vuex'
import VuexPersistence from 'vuex-persist'
import router from "@/router"

Vue.use(Vuex)

const vuexLocal = new VuexPersistence({
  storage: window.localStorage
})

export default new Vuex.Store({
  state: {
    token: null
  },
  getters: {
    getToken(state) {
      return state.token;
    }
  },
  mutations: {
    setToken(state, value) {
      state.token = value
    },
    clearToken(state) {
      state.token = null
    }
  },
  actions: {
    async logOut(context) {
      context.commit("clearToken")
      await router.replace({ name: "login" })
    }
  },
  modules: {
  },
  plugins: [ vuexLocal.plugin ]
})
