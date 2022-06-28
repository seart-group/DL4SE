<template>
  <fragment>
    <header>
      <b-smart-navbar />
    </header>
    <main id="app">
      <router-view />
    </main>
  </fragment>
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