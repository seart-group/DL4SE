import Vue from 'vue'
import VueRouter from 'vue-router'
import store from "@/store"
import axios from "@/axios"
import HomeView from '@/views/HomeView'
import LogInView from '@/views/LogInView'
import DashboardView from '@/views/DashboardView'
import RegisterView from "@/views/RegisterView"
import VerifyView from "@/views/VerifyView"
import NotFoundView from "@/views/NotFoundView"
import TaskCreateView from "@/views/TaskCreateView"
import DownloadView from "@/views/DownloadView"

Vue.use(VueRouter)

const sessionRestore = (_to, _from, next) => {
  const token = store.getters.getToken
  if (token) next({ name: 'dashboard' })
  else next()
}

const authCheck = async (_to, _from, next) => {
  await axios.get("/user")
      .then(next)
      .catch((err) => {
        const code = err.response.status
        if (code === 401) {
          store.commit("clearToken")
          next({ name: "login" })
        } else {
          next({ name: "home" })
        }
      })
}

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: {
      public: true
    }
  },
  {
    path: '/login',
    name: 'login',
    component: LogInView,
    beforeEnter: sessionRestore,
    meta: {
      public: true
    }
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: {
      public: true
    }
  },
  {
    path: '/verify/:token',
    name: 'verify',
    component: VerifyView,
    props: true,
    meta: {
      public: true
    }
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: DashboardView,
    beforeEnter: authCheck,
    meta: {
      public: false
    }
  },
  {
    path: '/task/:uuid?',
    name: 'task',
    component: TaskCreateView,
    beforeEnter: authCheck,
    props: true,
    meta: {
      public: false
    }
  },
  {
    path: '/download/:uuid',
    name: 'download',
    component: DownloadView,
    beforeEnter: authCheck,
    props: true,
    meta: {
      public: false
    }
  },
  {
    path: '/:pathMatch(.*)*',
    component: NotFoundView,
    meta: {
      public: true
    }
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
