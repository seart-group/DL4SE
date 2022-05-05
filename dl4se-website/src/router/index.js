import Vue from 'vue'
import VueRouter from 'vue-router'
import LogInView from '@/views/LogInView'
import ProfileView from '@/views/ProfileView'

Vue.use(VueRouter)

const routes = [
  {
    path: '',
    redirect: 'login'
  },
  {
    path: '/login',
    name: 'login',
    component: LogInView
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileView
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
