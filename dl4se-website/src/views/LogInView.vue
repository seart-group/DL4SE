<template>
  <div class="login">
    <h1>Log In</h1>
    <text-input-form
        :inputs="inputs"
        :api-target="apiTarget"
    />
  </div>
</template>

<script>
import TextInputForm from '@/components/TextInputForm.vue';

export default {
  components: {
    TextInputForm
  },
  data () {
    return {
      apiTarget: "https://localhost:8080/api/user/login",
      inputs : [
        {
          label: "Email",
          type: "email",
          key: "email",
          value: "",
          placeholder: "example@email.com"
        },
        {
          label: "Password",
          type: "password",
          key: "password",
          value: "",
          placeholder: ""
        }
      ]
    }
  }
}
</script>
