<template>
  <fragment>
    <header>
      <b-smart-navbar brand="DL4SE"
                      v-show="!isHomePage"
                      :dropdown-show="isPrivatePage"
      >
        <template #nav-items>
          <b-nav-item :to="{ name: 'home' }" :active="isOnPage('home')">Home</b-nav-item>
          <b-nav-item disabled>Stats</b-nav-item>
          <b-nav-item :to="{ name: 'about' }" :active="isOnPage('about')">About</b-nav-item>
          <b-nav-item :to="{ name: 'docs' }" :active="isOnPage('docs')">Docs</b-nav-item>
        </template>
        <template #dropdown-items>
          <b-dropdown-item disabled>Profile</b-dropdown-item>
          <b-dropdown-item :to="{ name: 'dashboard' }">Dashboard</b-dropdown-item>
          <b-dropdown-divider />
          <b-dropdown-item @click="showLogOutModal">Log Out</b-dropdown-item>
        </template>
      </b-smart-navbar>
    </header>
    <main>
      <router-view class="router-view" />
    </main>
    <footer>
      <b-footer />
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
    isHomePage() {
      return this.isOnPage('home')
    },
    isPrivatePage() {
      return !this.$route.meta.public
    }
  },
  methods: {
    isOnPage(name) {
      return this.$route.name === name
    },
    showLogOutModal() {
      this.showConfirmModal(
          "Log Out",
          "Any unsaved changes will be lost. Are you sure you want to continue?"
      ).then((confirmed) => {
        if (confirmed) this.$store.dispatch("logOut")
      })
    }
  }
}
</script>

<style lang="sass">
#app
  -webkit-font-smoothing: antialiased
  -moz-osx-font-smoothing: grayscale
</style>