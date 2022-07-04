<template>
  <fragment>
    <header>
      <b-smart-navbar />
    </header>
    <main id="app">
      <router-view />
    </main>
    <footer>
      <b-footer />
    </footer>
  </fragment>
</template>

<script>
import BFooter from '@/components/Footer'
import BSmartNavbar from "@/components/SmartNavbar"

export default {
  components: { BFooter, BSmartNavbar }
}
</script>

<style lang="sass">
#app
  -webkit-font-smoothing: antialiased
  -moz-osx-font-smoothing: grayscale
</style>