import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token: ""
  },
  getters: {
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
  }
})
