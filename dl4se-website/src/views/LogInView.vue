<template>
  <div class="login">
    <h1>Log In</h1>
    <text-input-form
        :inputs="inputs"
        :api-target="apiTarget"
        :success-handler="successHandler"
        :failure-handler="failureHandler"
    />
  </div>
</template>

<script>
import TextInputForm from '@/components/TextInputForm';

export default {
  components: {
    TextInputForm
  },
  data () {
    return {
      apiTarget: "https://localhost:8080/api/user/login",
      successHandler: (response) => {
        const token = response.data
        this.$store.commit("setToken", token)
        this.inputs.forEach((input) => { input.value = "" })
        this.$router.push('/profile')
      },
      failureHandler: (err) => {
        const status = err.response.status
        // TODO 04.05.22: Display popups for errors
        switch (status) {
          case 400:
            console.log("Form data invalid!")
            break
          case 401:
            console.log("Invalid credentials!")
            break
          default:
            console.log("An error has occurred! Status code: " + status)
        }
      },
      inputs : [
        {
          label: "Email",
          type: "email",
          key: "email",
          value: "",
          required: true,
          placeholder: "example@email.com"
        },
        {
          label: "Password",
          type: "password",
          key: "password",
          value: "",
          required: true,
          placeholder: ""
        }
      ]
    }
  }
}
</script>
