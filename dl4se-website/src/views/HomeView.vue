<template>
  <div id="home">
    <div class="card-stack">
      <b-card v-for="({title, description, linksTo, needsConnection}, idx) in cards"
              :key="idx" :class="cardClasses(idx)" no-body
      >
        <b-card-body>
          <b-link :disabled="needsConnection && !connected"
                  class="card-link text-secondary"
                  :to="linksTo"
          >
            <h4 class="card-title">{{ title }}</h4>
          </b-link>
          <p class="card-text">{{ description }}</p>
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
  methods: {
    cardClasses(idx) {
      return {
        'mt-4': idx > 0,
        'mb-4': idx < this.cards.length - 1,
        'border': true,
        'rounded-0': true,
        'card-background': true
      }
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