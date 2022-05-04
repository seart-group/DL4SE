import {createStore} from 'vuex'

export default createStore({
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
