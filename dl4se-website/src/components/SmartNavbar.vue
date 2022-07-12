<template>
  <b-navbar id="smart-navbar" v-show="!onDefaultPage"
            class="smart-navbar" sticky toggleable="sm"
  >
    <b-navbar-brand>DL4SE</b-navbar-brand>
    <b-navbar-toggle target="smart-navbar-collapse" />
    <b-collapse is-nav id="smart-navbar-collapse">
      <b-navbar-nav>
        <b-nav-item :to="{ name: 'home' }">Home</b-nav-item>
        <b-nav-item>Stats</b-nav-item>
        <b-nav-item>About</b-nav-item>
      </b-navbar-nav>
      <b-navbar-nav v-if="!onPublicPage" class="ml-auto">
        <b-nav-item-dropdown right>
          <template #button-content>
            <b-icon-person-fill />
          </template>
          <b-dropdown-item>Profile</b-dropdown-item>
          <b-dropdown-item :to="{ name: 'dashboard' }">Dashboard</b-dropdown-item>
          <b-dropdown-divider />
          <b-dropdown-item @click="showLogOutModal">Log Out</b-dropdown-item>
        </b-nav-item-dropdown>
      </b-navbar-nav>
    </b-collapse>
  </b-navbar>
</template>

<script>
import bootstrapMixin from "@/mixins/bootstrapMixin"

export default {
  name: "b-smart-navbar",
  mixins: [ bootstrapMixin ],
  computed: {
    onDefaultPage() {
      return this.$route.name === 'home'
    },
    onPublicPage() {
      return this.$route.meta.public
    }
  },
  methods: {
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