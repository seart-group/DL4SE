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
  async beforeMount() {
    await this.$http.get("/").catch(() => {
      this.connected = false
      this.appendToast(
          "Server Connection Refused",
          "The DL4SE server is currently unavailable. Please try accessing the site later.",
          "danger"
      )
    })
  },
  data() {
    return {
      connected: true,
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