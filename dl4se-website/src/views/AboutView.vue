<template>
  <fragment>
    <about />
    <transition name="fade">
      <b-back-to-top target="#about" :offset="-225" v-show="showBackToTop" />
    </transition>
  </fragment>
</template>

<script>
import About from "@/partials/About.vue";
import BBackToTop from "@/components/BackToTop";
import scrollMixin from "@/mixins/scrollMixin";

export default {
  components: { About, BBackToTop },
  mixins: [scrollMixin],
  computed: {
    showBackToTop() {
      return this.scroll.y > 100;
    },
  },
  head() {
    return {
      title: "About",
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/view/about.sass" />
