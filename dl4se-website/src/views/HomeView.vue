<template>
  <div id="home" class="d-flex flex-column justify-content-between">
    <section id="logo" v-if="!loggedIn">
      <b-container>
        <b-row align-h="center">
          <b-col cols="auto">
            <h1 v-aos.once="{ animation: 'fade', duration: 1000 }" class="branding">
              <span class="negative">DATA</span>
              <span class="positive">HUB</span>
            </h1>
          </b-col>
        </b-row>
        <b-row align-h="center">
          <b-col cols="auto">
            <b-link v-scroll-to="'#summary'" v-aos.once="{ animation: 'fade', duration: 1000, delay: 2500 }">
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
            <b-text-carousel>
              <b-text-carousel-slide content-class="text-justify">
                <template #title>Simple Dataset Construction</template>
                <template #content>
                  Our platform enables you to effortlessly create large-scale datasets for running MSR studies or
                  training DL models to automate SE tasks. Simply use our forms to specify the characteristics of the
                  dataset you want to build.
                </template>
              </b-text-carousel-slide>
              <b-text-carousel-slide content-class="text-justify">
                <template #title>Continuously Up-To-Date</template>
                <template #content>
                  Our crawlers work continuously to provide data aligned with the source. We currently mine Java and
                  Python source code from open-source repositories on GitHub. We are working on adding more languages
                  and features.
                </template>
              </b-text-carousel-slide>
              <b-text-carousel-slide content-class="text-justify">
                <template #title>Free &amp; Open-Source</template>
                <template #content>
                  Register for free today to instantly access all our dataset construction features. The entire project
                  is also open-source. View the source code or suggest improvements on the project's
                  <a href="https://github.com/seart-group/DL4SE" target="_blank">GitHub page</a>.
                </template>
              </b-text-carousel-slide>
            </b-text-carousel>
          </b-col>
        </b-row>
        <b-row align-h="center">
          <b-col cols="auto">
            <b-link :to="{ name: 'dashboard' }" :disabled="!connected">
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
import BTextCarouselSlide from "@/components/TextCarouselSlide";

export default {
  components: {
    BBackToTop,
    BTextCarousel,
    BTextCarouselSlide,
  },
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
};
</script>

<style scoped lang="sass" src="@/assets/styles/view/home.sass" />
