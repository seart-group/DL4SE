<template>
  <fragment>
    <documentation />
    <transition name="fade">
      <b-back-to-top target="#documentation" :offset="-225" v-show="showBackToTop" />
    </transition>
  </fragment>
</template>

<script>
import Documentation from "@/partials/Documentation";
import BBackToTop from "@/components/BackToTop";
import scrollMixin from "@/mixins/scrollMixin";

export default {
  components: { Documentation, BBackToTop },
  mixins: [scrollMixin],
  computed: {
    showBackToTop() {
      return this.scroll.y > 100;
    },
  },
  head() {
    return {
      title: "Documentation",
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/view/documentation.sass" />
