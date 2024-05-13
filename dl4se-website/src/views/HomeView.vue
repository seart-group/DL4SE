<template>
  <div id="home" class="d-flex flex-column justify-content-between">
    <section id="logo" v-if="!loggedIn">
      <b-container>
        <b-row align-h="center">
          <b-col cols="auto">
            <div v-aos.once="{ animation: 'fade', duration: 1000 }" class="branding">
              <span class="negative">SE</span>
              <span class="positive">ART</span>
            </div>
          </b-col>
        </b-row>
        <b-row align-h="center">
          <b-col cols="auto">
            <div v-aos.once="{ animation: 'fade', duration: 1000, delay: 1000 }" class="tagline">Dataset Hub</div>
          </b-col>
        </b-row>
        <b-row align-h="center">
          <b-col cols="auto">
            <b-link
              v-scroll-to="'#summary'"
              v-aos.once="{ animation: 'fade', duration: 1000, delay: 2500 }"
              class="text-secondary text-decoration-none"
            >
              <b-icon-chevron-double-down />
              Scroll For More Info
              <b-icon-chevron-double-down />
            </b-link>
          </b-col>
        </b-row>
      </b-container>
    </section>
    <section id="summary">
      <b-container>
        <b-row align-h="center">
          <b-col lg="6" md="9" cols="12">
            <b-text-carousel :slides="slides" />
          </b-col>
        </b-row>
        <b-row align-h="center">
          <b-col cols="auto">
            <b-link :to="{ name: 'dashboard' }" :disabled="!connected" class="text-secondary text-decoration-none">
              <b-icon-chevron-double-right />
              Get Started
              <b-icon-chevron-double-right />
            </b-link>
          </b-col>
        </b-row>
      </b-container>
    </section>
    <transition name="fade">
      <b-back-to-top target="#home" :offset="-225" v-show="showBackToTop" />
    </transition>
  </div>
</template>

<script>
import bootstrapMixin from "@/mixins/bootstrapMixin";
import scrollMixin from "@/mixins/scrollMixin";
import BBackToTop from "@/components/BackToTop";
import BTextCarousel from "@/components/TextCarousel";

export default {
  components: { BBackToTop, BTextCarousel },
  mixins: [bootstrapMixin, scrollMixin],
  props: {
    connected: Boolean,
    loggedIn: Boolean,
  },
  computed: {
    showBackToTop() {
      return this.scroll.y > 100;
    },
  },
  data() {
    return {
      slides: {
        "Simple Dataset Construction": `
        Our platform allows you to easily create large-scale datasets that can be used to either run MSR studies or to
        train DL models to automate SE tasks. Use our forms to define the characteristics of the dataset you would like
        to build.`,
        "Continuously Up-To-Date": `
        Our crawlers work around the clock to ensure that you are served data that is in line with the source.
        We currently only mine Java and Python source code from open source repositories hosted on GitHub.
        We are working on integrating additional languages and features.`,
        "Free & Open-Source": `
        Register for free today and get instant access to all the dataset construction features of our platform.
        The entire project is also open-source. You can view the source code or suggest improvements on the project
        <a href="https://github.com/seart-group/DL4SE" target="_blank" class="text-secondary">GitHub page</a>.`,
      },
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/view/home.sass" />
