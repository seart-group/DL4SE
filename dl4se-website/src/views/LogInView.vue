<template>
  <div class="login">
    <h1 class="m-4">Log In</h1>
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
  methods: {
    appendToast(title, message, variant) {
      this.$bvToast.toast(message, {
        title: title,
        variant: variant,
        toaster: "b-toaster-top-right",
        autoHideDelay: 4500,
        appendToast: true
      })
    }
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
        switch (status) {
          case 400:
            this.appendToast("Error", "Invalid form inputs. ", "danger")
            break
          case 401:
            this.appendToast("Error", "Invalid login credentials.", "danger")
            break
          default:
            this.appendToast("Error", "An unexpected server error has occurred. Please try again later.", "danger")
            break
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
