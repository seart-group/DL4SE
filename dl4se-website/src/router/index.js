import Vue from "vue";
import VueRouter from "vue-router";
import axios from "@/axios";

const HomeView = () => import("@/views/HomeView");
const LogInView = () => import("@/views/LogInView");
const DashboardView = () => import("@/views/DashboardView");
const ProfileView = () => import("@/views/ProfileView");
const RegisterView = () => import("@/views/RegisterView");
const VerifyView = () => import("@/views/VerifyView");
const NotFoundView = () => import("@/views/NotFoundView");
const CodeTaskView = () => import("@/views/CodeTaskView");
const CodeSearchView = () => import("@/views/CodeSearchView");
const CodeInstanceView = () => import("@/views/CodeInstanceView");
const DownloadView = () => import("@/views/DownloadView");
const AdminView = () => import("@/views/AdminView");
const StatisticsView = () => import("@/views/StatisticsView");
const AboutView = () => import("@/views/AboutView");
const DocumentationView = () => import("@/views/DocumentationView");
const ForgotPasswordView = () => import("@/views/ForgotPasswordView");
const ResetPasswordView = () => import("@/views/ResetPasswordView");

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
    path: "/code/search",
    name: "search",
    component: CodeSearchView,
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
    path: "/code/instance/:id",
    name: "instance",
    component: CodeInstanceView,
    props: (route) => {
      return { ...route.params, ...{ id: Number.parseInt(route.params.id, 10) || undefined } };
    },
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
    redirect: "/statistics",
  },
  {
    path: "/statistics",
    name: "statistics",
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
    redirect: "/documentation",
  },
  {
    path: "/documentation",
    name: "documentation",
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
