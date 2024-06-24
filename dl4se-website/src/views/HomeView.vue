<template>
  <div id="home" class="d-flex flex-column justify-content-between">
    <section>
      <b-container>
        <b-row align-h="center">
          <b-col cols="auto">
            <h1 class="branding">
              <span class="negative">DATA</span>
              <span class="positive">HUB</span>
            </h1>
          </b-col>
        </b-row>
        <b-row align-h="center">
          <b-col lg="6" md="9" cols="12">
            <b-text-carousel>
              <b-text-carousel-slide title-tag="h2" body-class="text-justify">
                <template #title>Simple Dataset Construction</template>
                <template #body>
                  Our platform enables you to effortlessly create large-scale datasets for running MSR studies or
                  training DL models to automate SE tasks. Simply use our forms to specify the characteristics of the
                  dataset you want to build.
                </template>
              </b-text-carousel-slide>
              <b-text-carousel-slide title-tag="h2" body-class="text-justify">
                <template #title>Continuously Up-To-Date</template>
                <template #body>
                  Our crawlers work continuously to provide data aligned with the source. We currently mine Java and
                  Python source code from open-source repositories on GitHub. We are working on adding more languages
                  and features.
                </template>
              </b-text-carousel-slide>
              <b-text-carousel-slide title-tag="h2" body-class="text-justify">
                <template #title>Free &amp; Open-Source</template>
                <template #body>
                  Register for free today to instantly access all our dataset construction features. The entire project
                  is also open-source. View the source code or suggest improvements on the project's
                  <a href="https://github.com/seart-group/DL4SE" target="_blank">GitHub page</a>.
                </template>
              </b-text-carousel-slide>
            </b-text-carousel>
          </b-col>
        </b-row>
      </b-container>
    </section>
  </div>
</template>

<script>
import bootstrapMixin from "@/mixins/bootstrapMixin";
import BTextCarousel from "@/components/TextCarousel";
import BTextCarouselSlide from "@/components/TextCarouselSlide";

export default {
  components: { BTextCarousel, BTextCarouselSlide },
  mixins: [bootstrapMixin],
  props: {
    connected: Boolean,
    loggedIn: Boolean,
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/view/home.sass" />
