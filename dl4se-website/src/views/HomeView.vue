<template>
  <div id="home">
    <h1 class="page-title">Welcome to DL4SE!</h1>
    <div class="card-stack">
      <b-card
          v-for="(card, idx) in cards" :key="idx"
          no-body :class="cardClasses(idx)"
      >
        <b-card-body>
          <b-link :to="card.linksTo" class="text-secondary">
            <h4 class="card-title">{{ card.title }}</h4>
          </b-link>
          <p class="card-text">{{ card.description }}</p>
        </b-card-body>
      </b-card>
    </div>
  </div>
</template>

<script>
export default {
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