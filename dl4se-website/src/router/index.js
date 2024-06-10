import Vue from "vue";
import VueRouter from "vue-router";
import axios from "@/axios";
import HomeView from "@/views/HomeView";
import LogInView from "@/views/LogInView";
import DashboardView from "@/views/DashboardView";
import ProfileView from "@/views/ProfileView";
import RegisterView from "@/views/RegisterView";
import VerifyView from "@/views/VerifyView";
import NotFoundView from "@/views/NotFoundView";
import CodeTaskView from "@/views/CodeTaskView";
import DownloadView from "@/views/DownloadView";
import AdminView from "@/views/AdminView";
import StatisticsView from "@/views/StatisticsView";
import AboutView from "@/views/AboutView";
import DocumentationView from "@/views/DocumentationView";
import ForgotPasswordView from "@/views/ForgotPasswordView";
import ResetPasswordView from "@/views/ResetPasswordView";

Vue.use(VueRouter);

const isAdmin = async (_to, _from, next) => {
  await axios
    .get("/admin")
    .then(() => next())
    .catch(() => router.replace({ name: "home" }));
};

const routes = [
  {
    path: "/",
    name: "home",
    component: HomeView,
    meta: {
      public: true,
    },
  },
  {
    path: "/login",
    name: "login",
    component: LogInView,
    meta: {
      public: true,
    },
  },
  {
    path: "/register",
    name: "register",
    component: RegisterView,
    meta: {
      public: true,
    },
  },
  {
    path: "/verify/:token",
    name: "verify",
    component: VerifyView,
    props: true,
    meta: {
      public: true,
    },
  },
  {
    path: "/password/request",
    name: "forgot",
    component: ForgotPasswordView,
    meta: {
      public: true,
    },
  },
  {
    path: "/password/reset/:token",
    name: "reset",
    component: ResetPasswordView,
    props: true,
    meta: {
      public: true,
    },
  },
  {
    path: "/dashboard",
    name: "dashboard",
    component: DashboardView,
    meta: {
      public: false,
    },
  },
  {
    path: "/profile",
    name: "profile",
    component: ProfileView,
    meta: {
      public: false,
    },
  },
  {
    path: "/code/:uuid?",
    name: "code",
    component: CodeTaskView,
    props: true,
    meta: {
      public: false,
    },
  },
  {
    path: "/download/:uuid",
    name: "download",
    component: DownloadView,
    props: true,
    meta: {
      public: false,
    },
  },
  {
    path: "/admin",
    name: "admin",
    component: AdminView,
    beforeEnter: isAdmin,
    meta: {
      public: false,
    },
  },
  {
    path: "/stats",
    name: "stats",
    component: StatisticsView,
    meta: {
      public: true,
    },
  },
  {
    path: "/about",
    name: "about",
    component: AboutView,
    meta: {
      public: true,
    },
  },
  {
    path: "/docs",
    name: "docs",
    component: DocumentationView,
    meta: {
      public: true,
    },
  },
  {
    path: "/:pathMatch(.*)*",
    component: NotFoundView,
    meta: {
      public: true,
    },
  },
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
  scrollBehavior: ({ hash }) => {
    return hash ? { selector: hash } : undefined;
  },
});

router.beforeEach(async (to, _from, next) => {
  if (to.meta.public) {
    next();
  } else {
    await axios
      .get("/user")
      .then(() => next())
      .catch((err) => {
        const code = err.response.status;
        if (code === 401) {
          const wasLoggedIn = !!router.app.$store.getters.getToken;
          if (wasLoggedIn) {
            router.app.$store.dispatch("logOut", to.name).then(() => {
              router.app.$bvToast.toast("Your session has expired, please log in again.", {
                toaster: "b-toaster-top-right",
                title: "Logged Out",
                variant: "secondary",
                autoHideDelay: 3000,
                appendToast: true,
                solid: true,
              });
            });
          } else {
            next({ name: "login", query: { target: to.name } });
          }
        } else {
          next({ name: "home" });
        }
      });
  }
});

export default router;
