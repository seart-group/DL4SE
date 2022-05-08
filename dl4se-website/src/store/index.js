import Vue from 'vue'
import Vuex from 'vuex'
import VuexPersistence from 'vuex-persist'

Vue.use(Vuex)

const vuexLocal = new VuexPersistence({
  storage: window.localStorage
})

export default new Vuex.Store({
  state: {
    token: ""
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
      state.token = ""
    }
  },
  actions: {
  },
  modules: {
  },
  plugins: [ vuexLocal.plugin ]
})
