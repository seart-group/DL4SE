<template>
  <div id="app">
    <b-smart-navbar />
    <router-view />
  </div>
</template>

<script>
import BSmartNavbar from "@/components/SmartNavbar"

export default {
  components: { BSmartNavbar }
}
</script>

<style lang="sass">
#app
  -webkit-font-smoothing: antialiased
  -moz-osx-font-smoothing: grayscale
</style>