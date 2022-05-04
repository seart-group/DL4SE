import {createRouter, createWebHistory} from 'vue-router'
import LogInView from '@/views/LogInView.vue'
import ProfileView from '@/views/ProfileView.vue'

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

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
