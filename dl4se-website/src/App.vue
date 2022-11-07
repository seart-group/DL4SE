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
          <b-nav-item :to="{ name: 'stats' }" :active="isOnPage('stats')">Statistics</b-nav-item>
          <b-nav-item :to="{ name: 'about' }" :active="isOnPage('about')">About</b-nav-item>
          <b-nav-item :to="{ name: 'docs' }" :active="isOnPage('docs')">Tutorial</b-nav-item>
        </template>
        <template #nav-items-right>
          <b-nav-item-dropdown right v-if="$store.getters.getToken">
            <template #button-content>
              <b-icon-person-fill />
            </template>
            <b-dropdown-item disabled>Profile</b-dropdown-item>
            <b-dropdown-item :to="{ name: 'dashboard' }">Dashboard</b-dropdown-item>
            <b-dropdown-divider />
            <b-dropdown-item @click="showLogOutModal">Log Out</b-dropdown-item>
          </b-nav-item-dropdown>
          <template v-else>
            <b-nav-item :to="{ name: 'login' }" :active="isOnPage('login')">Log In</b-nav-item>
            <b-nav-item :to="{ name: 'register' }" :active="isOnPage('register')">Register</b-nav-item>
          </template>
        </template>
      </b-smart-navbar>
    </header>
    <main>
      <router-view class="router-view" />
    </main>
    <footer>
      <b-footer :authors="authors" :organisation="organisation" />
    </footer>
  </fragment>
</template>

<script>
import bootstrapMixin from "@/mixins/bootstrapMixin"
import BFooter from '@/components/Footer'
import BSmartNavbar from "@/components/SmartNavbar"

export default {
  components: { BFooter, BSmartNavbar },
  mixins: [ bootstrapMixin ],
  computed: {
    currentPage() {
      return this.$route.name
    }
  },
  methods: {
    isOnPage(name) {
      return this.currentPage === name
    },
    showLogOutModal() {
      this.showConfirmModal(
          "Log Out",
          "Any unsaved changes will be lost. Are you sure you want to continue?"
      ).then((confirmed) => {
        if (confirmed) this.$store.dispatch("logOut")
      })
    }
  },
  data() {
    return {
      authors: [
        {
          name: "Ozren Dabić",
          url: "https://dabico.github.io/"
        },
        {
          name: "Emad Aghajani",
          url: "https://emadpres.github.io/"
        },
        {
          name: "Gabriele Bavota",
          url: "https://inf.usi.ch/faculty/bavota/"
        }
      ],
      organisation: {
        name: "SEART",
        url: "https://seart.si.usi.ch/"
      }
    }
  }
}
</script>

<style lang="sass">
#app
  -webkit-font-smoothing: antialiased
  -moz-osx-font-smoothing: grayscale
</style>