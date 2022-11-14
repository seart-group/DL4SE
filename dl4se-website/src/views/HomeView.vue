<template>
  <div id="home">
    <div id="logo" class="logo fullscreen">
      <transition name="fade" appear>
        <div class="logo-image">
          <span class="logo-image-negative">DL</span>
          <span class="logo-image-positive">4SE</span>
        </div>
      </transition>
      <transition name="fade" appear>
        <div class="logo-tagline transition-delay-1000">
          <span> Deep Learning For Software Engineering</span>
        </div>
      </transition>
      <transition name="fade" appear>
        <div class="logo-jump transition-delay-1500">
          <b-link v-scroll-to="'#datasets'" class="logo-jump-link">
            <b-icon-chevron-double-down />
            Choose Your Dataset
            <b-icon-chevron-double-down />
          </b-link>
        </div>
      </transition>
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