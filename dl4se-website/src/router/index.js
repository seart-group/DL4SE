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
import AboutView from "@/views/AboutView"
import ForgotPasswordView from "@/views/ForgotPasswordView";

Vue.use(VueRouter)

const sessionRestore = (_to, _from, next) => {
  const token = store.getters.getToken
  if (token) next({ name: 'dashboard' })
  else next()
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
    path: '/password/request',
    name: 'forgot',
    component: ForgotPasswordView,
    meta: {
      public: true
    }
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: DashboardView,
    meta: {
      public: false
    }
  },
  {
    path: '/task/:uuid?',
    name: 'task',
    component: TaskCreateView,
    props: true,
    meta: {
      public: false
    }
  },
  {
    path: '/download/:uuid',
    name: 'download',
    component: DownloadView,
    props: true,
    meta: {
      public: false
    }
  },
  {
    path: '/about',
    name: 'about',
    component: AboutView,
    meta: {
      public: true
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
  routes,
  scrollBehavior: (to) => {
    if (to.hash) {
      return { selector: to.hash }
    }
  }
})

router.beforeEach(async (to, _from, next) => {
  if (to.meta.public) {
    next()
  } else {
    await axios.get("/user")
        .then(() => next())
        .catch((err) => {
          const code = err.response.status
          if (code === 401) {
            const wasLoggedIn = !!router.app.$store.getters.getToken
            router.app.$store.dispatch("logOut").then(() => {
              if (wasLoggedIn) {
                router.app.$bvToast.toast(
                    "Your session has expired, please log in again.",
                    {
                      toaster: "b-toaster-top-right",
                      title: "Logged Out",
                      variant: "secondary",
                      autoHideDelay: 3000,
                      appendToast: true,
                      solid: true
                    }
                )
              }
            })
          } else {
            next({ name: "home" })
          }
        })
  }
})

export default router
