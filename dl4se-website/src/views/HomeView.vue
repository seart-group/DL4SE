<template>
  <div id="home" class="home">
    <div id="logo" class="logo fullscreen">
      <b-container>
        <b-row align-h="center">
          <b-col cols="auto">
            <div v-aos.once="{ animation: 'fade', duration: 1000 }" class="logo-image">
              <span class="logo-image-negative">DL</span>
              <span class="logo-image-positive">4SE</span>
            </div>
          </b-col>
        </b-row>
        <b-row align-h="center">
          <b-col cols="auto">
            <div v-aos.once="{ animation: 'fade', duration: 1000, delay: 1000 }" class="logo-tagline">
              Deep Learning For Software Engineering
            </div>
          </b-col>
        </b-row>
        <b-row align-h="center">
          <b-col cols="auto">
            <b-link v-scroll-to="'#summary'"
                    v-aos.once="{ animation: 'fade', duration: 1000, delay: 2500 }"
                    class="home-scroll-link"
            >
              <b-icon-chevron-double-down />
              Scroll For More Info
              <b-icon-chevron-double-down />
            </b-link>
          </b-col>
        </b-row>
      </b-container>
    </div>
    <div id="summary" class="summary fullscreen">
      <b-container>
        <b-row align-h="center">
          <b-col lg="6" md="9" cols="12">
            <p class="text-justify" v-aos.once="{ animation: 'fade', duration: 1000 }">
              The DL4SE tool allows to easily create large-scale datasets that can be used to either run MSR studies or to
              train DL models to automate SE tasks. Use our forms to define the characteristics of the dataset you would like
              to build.
            </p>
          </b-col>
        </b-row>
        <b-row align-h="center">
          <b-col cols="auto">
            <b-link v-scroll-to="'#datasets'"
                    v-aos.once="{ animation: 'fade', duration: 1000, delay: 2500 }"
                    class="home-scroll-link"
            >
              <b-icon-chevron-double-down />
              Create A Dataset
              <b-icon-chevron-double-down />
            </b-link>
          </b-col>
        </b-row>
      </b-container>
    </div>
    <div id="datasets" class="datasets fullscreen">
      <b-card v-for="({title, description, linksTo, needsConnection}, idx) in cards" :key="idx" no-body
              v-aos.once="{ animation: 'fade', duration: 1000, delay: (idx + 1) * 250 }"
              :class="{
                'border': true,
                'rounded-0': true,
                'card-background': true,
                'mb-3': idx === 0,
                'my-3': 0 < idx && idx < cards.length - 1,
                'mt-3': idx === cards.length - 1
              }"
      >
        <b-card-body>
          <b-card-title>
            <b-link :to="linksTo"
                    :disabled="needsConnection && !connected"
                    class="card-link text-secondary"
            >
              {{ title }}
            </b-link>
          </b-card-title>
          <b-card-text>
            {{ description }}
          </b-card-text>
        </b-card-body>
      </b-card>
    </div>
    <transition name="fade">
      <b-back-to-top target="#home" v-show="showBackToTop" />
    </transition>
  </div>
</template>

<script>
import bootstrapMixin from "@/mixins/bootstrapMixin"
import scrollMixin from "@/mixins/scrollMixin"
import BBackToTop from "@/components/BackToTop"

export default {
  components: { BBackToTop },
  mixins: [ bootstrapMixin, scrollMixin ],
  props: {
    connected: Boolean
  },
  computed: {
    showBackToTop() {
      return this.scroll.y > 100
    }
  },
  data() {
    return {
      cards: [
        {
          title: "Code Completion",
          description: "Generate a dataset for Deep Learning models specializing in code completion.",
          linksTo: { name: "task" },
          needsConnection: true
        }
      ]
    }
  }
}
</script>