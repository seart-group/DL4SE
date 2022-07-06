<template>
  <div id="home">
    <b-img :src="image" alt="DL4SE" center fluid class="logo-image" />
    <div class="card-stack">
      <b-card
          v-for="({title, description, linksTo}, idx) in cards" :key="idx"
          no-body :class="cardClasses(idx)"
      >
        <b-card-body>
          <b-link :to="linksTo" class="text-secondary">
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
      this.appendToast(
          "Server Connection Refused",
          "The DL4SE server is currently unavailable. Please try accessing the site later.",
          "danger"
      )
    })
  },
  data() {
    return {
      image: require('@/assets/img/logo.png'),
      cards: [
        {
          title: "Log In",
          description: "Start generating datasets for your models in just a few clicks!",
          linksTo: "login"
        },
        {
          title: "Register",
          description: "Don't have an account? Register for free today!",
          linksTo: "register"
        },
        {
          title: "About",
          description: "Want to learn more about the project?",
          linksTo: "about"
        }
      ]
    }
  }
}
</script>