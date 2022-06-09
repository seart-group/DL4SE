import Vue from 'vue'
import VueRouter from 'vue-router'
import HomeView from '@/views/HomeView'
import LogInView from '@/views/LogInView'
import DashboardView from '@/views/DashboardView'
import RegisterView from "@/views/RegisterView";
import VerifyView from "@/views/VerifyView";
import NotFoundView from "@/views/NotFoundView";
import TaskCreateView from "@/views/TaskCreateView";

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
    path: '/dashboard',
    name: 'dashboard',
    component: DashboardView,
  },
  {
    path: '/task',
    name: 'task',
    component: TaskCreateView
  },
  {
    path: '/:pathMatch(.*)*',
    component: NotFoundView
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
