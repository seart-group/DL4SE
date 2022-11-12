<template>
  <div id="home">
    <div class="logo">
      <transition name="fade" appear>
        <div class="logo-image">
          <span class="logo-image-negative">DL</span>
          <span class="logo-image-positive">4SE</span>
        </div>
      </transition>
      <transition name="fade" appear>
        <div class="logo-tagline">
          <span> Deep Learning For Software Engineering</span>
        </div>
      </transition>
      <transition name="fade" appear>
        <div class="logo-jump">
          <b-link v-scroll-to="'#datasets'" class="logo-jump-link">
            <b-icon-chevron-double-down />
            Choose Your Dataset
            <b-icon-chevron-double-down />
          </b-link>
        </div>
      </transition>
    </div>
    <div id="datasets" class="card-stack">
      <b-card v-for="({title, description, linksTo, needsConnection}, idx) in cards"
              :key="idx" class="border rounded-0 card-background my-4" no-body
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