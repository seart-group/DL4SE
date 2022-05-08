<template>
  <div class="home">
    <h1 class="page-title">Welcome to DL4SE!</h1>
    <b-card-group deck>
      <b-card no-body v-for="(section, idx) in sections" :key="idx">
        <b-card-body>
          <b-link :to="section.linksTo">
            <h4 class="card-title">{{ section.title }}</h4>
          </b-link>
          <p class="card-text">{{ section.description }}</p>
        </b-card-body>
      </b-card>
    </b-card-group>
  </div>
</template>

<script>
export default {
  data() {
    return {
      sections: [
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