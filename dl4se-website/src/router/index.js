import Vue from 'vue'
import VueRouter from 'vue-router'
import HomeView from '@/views/HomeView'
import LogInView from '@/views/LogInView'
import ProfileView from '@/views/ProfileView'
import RegisterView from "@/views/RegisterView";
import VerifyView from "@/views/VerifyView";

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/login',
    name: 'login',
    component: LogInView
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView
  },
  {
    path: '/verify/:token',
    name: 'verify',
    component: VerifyView,
    props: true
  },
  {
    path: '/profile/:uid',
    name: 'profile',
    component: ProfileView,
    props: true
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
