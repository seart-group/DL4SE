<template>
  <fragment>
    <header>
      <b-smart-navbar>
        <template #brand>
          <b-logo />
        </template>
        <template #nav-items-left>
          <b-nav-item :to="{ name: 'home' }" :active="isOnPage('home')"> Home </b-nav-item>
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
          <b-nav-item href="https://github.com/seart-group/dl4se" target="_blank">
            <b-icon-github class="d-none d-sm-inline" />
            <span class="d-inline d-sm-none"> GitHub </span>
          </b-nav-item>
        </template>
      </b-smart-navbar>
    </header>
    <main>
      <router-view :connected="connected" :logged-in="loggedIn" class="router-view" />
    </main>
    <footer class="bg-light-gray py-3">
      <b-container class="text-center">
        <span class="text-muted">&copy; 2022 - {{ new Date().getFullYear() }}</span>
      </b-container>
    </footer>
  </fragment>
</template>

<script>
import bootstrapMixin from "@/mixins/bootstrapMixin";
import BLogo from "@/components/Logo";
import BSmartNavbar from "@/components/SmartNavbar";

export default {
  components: { BLogo, BSmartNavbar },
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
    };
  },
};
</script>

<style lang="sass">
#app
  -webkit-font-smoothing: antialiased
  -moz-osx-font-smoothing: grayscale
</style>
