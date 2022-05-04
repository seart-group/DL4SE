import {createRouter, createWebHistory} from 'vue-router'
import LogInView from '@/views/LogInView.vue'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LogInView
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
