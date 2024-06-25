<template>
  <fragment>
    <header>
      <b-smart-navbar id="smart-navbar" toggleable="md" class="bg-light">
        <template #brand>
          <b-logo />
        </template>
        <template #nav-items-left>
          <b-nav-item
            v-for="navItem in navItems.left"
            :to="{ name: navItem.name }"
            :disabled="navItem.disabled"
            :active="isOnPage(navItem.name)"
            :key="navItem.name"
          >
            {{ startCase(navItem.name) }}
          </b-nav-item>
        </template>
        <template #nav-items-right>
          <template v-if="$store.getters.getToken">
            <b-nav-item
              v-for="navItem in navItems.right"
              :to="{ name: navItem.name }"
              :disabled="navItem.disabled"
              :active="isOnPage(navItem.name)"
              :key="navItem.name"
            >
              {{ startCase(navItem.name) }}
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
      <b-alert :show="updateExists" variant="dark" class="d-flex align-items-center bg-light border-0 column-gap-1">
        An update is available,
        <b-button variant="link" class="border-0 p-0" @click="refreshApp">click here!</b-button>
      </b-alert>
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
import formatterMixin from "@/mixins/formatterMixin";
import swMixin from "@/mixins/swMixin";
import BLogo from "@/components/Logo";
import BSmartNavbar from "@/components/SmartNavbar";

export default {
  components: { BLogo, BSmartNavbar },
  mixins: [bootstrapMixin, formatterMixin, swMixin],
  computed: {
    navItems: {
      get() {
        return {
          left: [
            {
              name: "home",
              disabled: false,
            },
            {
              name: "statistics",
              disabled: !this.connected,
            },
            {
              name: "documentation",
              disabled: false,
            },
            {
              name: "about",
              disabled: false,
            },
          ],
          right: [
            {
              name: "profile",
              disabled: !this.connected,
            },
            {
              name: "search",
              disabled: !this.connected,
            },
            {
              name: "dashboard",
              disabled: !this.connected,
            },
          ],
        };
      },
    },
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
