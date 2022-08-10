<template>
  <div id="home">
    <b-img :src="image" alt="DL4SE" class="logo-image" center fluid />
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
      image: require('@/assets/img/logo.png'),
      cards: [
        {
          title: "Log In",
          description: "Start generating datasets for your models in just a few clicks!",
          linksTo: "login",
          needsConnection: true
        },
        {
          title: "Register",
          description: "Don't have an account? Register for free today!",
          linksTo: "register",
          needsConnection: true
        },
        {
          title: "About",
          description: "Want to learn more about the project?",
          linksTo: "about",
          needsConnection: false
        }
      ]
    }
  }
}
</script>