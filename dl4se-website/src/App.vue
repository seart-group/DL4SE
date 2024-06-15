<template>
  <fragment>
    <header>
      <b-smart-navbar id="smart-navbar" toggleable="md" class="bg-light">
        <template #brand>
          <b-logo />
        </template>
        <template #nav-items-left>
          <b-nav-item :to="{ name: 'home' }" :active="isOnPage('home')">Home</b-nav-item>
          <b-nav-item :to="{ name: 'statistics' }" :active="isOnPage('statistics')" :disabled="!connected">
            Statistics
          </b-nav-item>
          <b-nav-item :to="{ name: 'documentation' }" :active="isOnPage('documentation')">Documentation</b-nav-item>
          <b-nav-item :to="{ name: 'about' }" :active="isOnPage('about')">About</b-nav-item>
        </template>
        <template #nav-items-right>
          <template v-if="$store.getters.getToken">
            <b-nav-item :to="{ name: 'profile' }" :active="isOnPage('profile')" :disabled="!connected">
              Profile
            </b-nav-item>
            <b-nav-item :to="{ name: 'dashboard' }" :active="isOnPage('dashboard')" :disabled="!connected">
              Dashboard
            </b-nav-item>
            <b-nav-item @click="showLogOutModal">Log Out</b-nav-item>
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
            <b-icon-github v-if="$screen.md" />
            <template v-else>GitHub</template>
          </b-nav-item>
        </template>
      </b-smart-navbar>
    </header>
    <main>
      <router-view :connected="connected" :logged-in="loggedIn" class="router-view" />
    </main>
    <footer>
      <div class="bg-light py-3">
        <b-container class="text-center">
          <span class="text-muted">&copy; 2022 - {{ new Date().getFullYear() }}</span>
        </b-container>
      </div>
    </footer>
  </fragment>
</template>

<script>
import { useHead } from "@unhead/vue";
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
      const pages = ["home", "login", "register"];
      return pages.some(this.isOnPage) ? undefined : this.currentPage;
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
        "The server is currently unreachable. Please try accessing the site later.",
        "danger",
      );
    });
  },
  setup() {
    useHead({
      titleTemplate: (title) => `Data Hub${title ? " | " + title : ""}`,
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
@import "node_modules/bootstrap/scss/_functions.scss"
@import "node_modules/bootstrap/scss/_variables.scss"

#app
  -webkit-font-smoothing: antialiased
  -moz-osx-font-smoothing: grayscale

.router-view
  padding-top: map-get($spacers, 4)!important
  padding-bottom: map-get($spacers, 4)!important

@media (min-width: 576px)
  .router-view
    padding-left: map-get($spacers, 4)!important
    padding-right: map-get($spacers, 4)!important
</style>
