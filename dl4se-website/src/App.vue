<template>
  <fragment>
    <header>
      <b-smart-navbar>
        <template #brand>
          <b-link :to="{ name: 'home' }" :active="isOnPage('home')" class="brand">
            <span class="brand-negative">DL</span>
            <span class="brand-positive">4SE</span>
          </b-link>
        </template>
        <template #nav-items-left>
          <b-nav-item :to="{ name: 'stats' }" :active="isOnPage('stats')" :disabled="!connected">
            Statistics
          </b-nav-item>
          <b-nav-item :to="{ name: 'about' }" :active="isOnPage('about')"> About </b-nav-item>
        </template>
        <template #nav-items-right>
          <template v-if="$store.getters.getToken">
            <b-nav-item :to="{ name: 'dashboard' }" :active="isOnPage('dashboard')" :disabled="!connected">
              Dashboard
            </b-nav-item>
            <b-nav-item @click="showLogOutModal" :disabled="!connected"> Log Out </b-nav-item>
          </template>
          <template v-else>
            <b-nav-item
              :to="{ name: 'login', query: { target: loginTarget } }"
              :active="isOnPage('login')"
              :disabled="!connected"
            >
              Log In
            </b-nav-item>
            <b-nav-item :to="{ name: 'register' }" :active="isOnPage('register')" :disabled="!connected">
              Register
            </b-nav-item>
          </template>
        </template>
      </b-smart-navbar>
    </header>
    <main>
      <router-view :connected="connected" :logged-in="loggedIn" class="router-view" />
    </main>
    <footer>
      <b-footer :authors="authors" :organisation="organisation" />
    </footer>
  </fragment>
</template>

<script>
import bootstrapMixin from "@/mixins/bootstrapMixin";
import BFooter from "@/components/Footer";
import BSmartNavbar from "@/components/SmartNavbar";

export default {
  components: { BFooter, BSmartNavbar },
  mixins: [bootstrapMixin],
  computed: {
    currentPage() {
      return this.$route.name;
    },
    loggedIn() {
      return !!this.$store.getters.getToken;
    },
    loginTarget() {
      const isHome = this.isOnPage("home");
      const isLogin = this.isOnPage("login");
      const isRegister = this.isOnPage("register");
      return isHome || isLogin || isRegister ? undefined : this.currentPage;
    },
  },
  methods: {
    isOnPage(name) {
      return this.currentPage === name;
    },
    showLogOutModal() {
      this.showConfirmModal("Log Out", "Any unsaved changes will be lost. Are you sure you want to continue?").then(
        (confirmed) => {
          if (confirmed) this.$store.dispatch("logOut");
        },
      );
    },
  },
  async beforeMount() {
    await this.$http.get("/").catch(() => {
      this.connected = false;
      this.appendToast(
        "Server Connection Refused",
        "The DL4SE server is currently unavailable. Please try accessing the site later.",
        "danger",
      );
    });
  },
  data() {
    return {
      interval: undefined,
      connected: true,
      authors: [
        {
          name: "Ozren Dabić",
          url: "https://dabico.github.io/",
        },
        {
          name: "Emad Aghajani",
          url: "https://emadpres.github.io/",
        },
        {
          name: "Gabriele Bavota",
          url: "https://inf.usi.ch/faculty/bavota/",
        },
      ],
      organisation: {
        name: "SEART",
        url: "https://seart.si.usi.ch/",
      },
    };
  },
};
</script>

<style lang="sass">
#app
  -webkit-font-smoothing: antialiased
  -moz-osx-font-smoothing: grayscale
</style>
