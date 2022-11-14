<template>
  <div id="home" class="home">
    <div id="logo" class="logo fullscreen">
      <b-container>
        <b-row align-h="center">
          <b-col cols="auto">
            <transition name="fade" appear>
              <div class="logo-image">
                <span class="logo-image-negative">DL</span>
                <span class="logo-image-positive">4SE</span>
              </div>
            </transition>
          </b-col>
        </b-row>
        <b-row align-h="center">
          <b-col cols="auto">
            <transition name="fade" appear>
              <div class="logo-tagline transition-delay-1000">
                Deep Learning For Software Engineering
              </div>
            </transition>
          </b-col>
        </b-row>
        <b-row align-h="center">
          <b-col cols="auto">
            <transition name="fade" appear>
              <div class="transition-delay-3000">
                <b-link v-scroll-to="'#summary'" class="home-scroll-link">
                  <b-icon-chevron-double-down />
                  Scroll For More Info
                  <b-icon-chevron-double-down />
                </b-link>
              </div>
            </transition>
          </b-col>
        </b-row>
      </b-container>
    </div>
    <div id="summary" class="summary fullscreen">
      <b-container>
        <b-row align-h="center">
          <b-col lg="6" md="9" cols="12">
            <p class="text-justify">
              The DL4SE tool allows to easily create large-scale datasets that can be used to either run MSR studies or to
              train DL models to automate SE tasks. Use our forms to define the characteristics of the dataset you would like
              to build.
            </p>
          </b-col>
        </b-row>
        <b-row align-h="center">
          <b-col cols="auto">
            <b-link v-scroll-to="'#datasets'" class="home-scroll-link">
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
  </div>
</template>

<script>
import bootstrapMixin from "@/mixins/bootstrapMixin"

export default {
  mixins: [ bootstrapMixin ],
  props: {
    connected: Boolean
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