<template>
  <div id="login">
    <h1 class="page-title">Log In</h1>
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
import bootstrapMixin from '@/mixins/bootstrapMixin'

export default {
  components: {
    TextInputForm
  },
  mixins: [ bootstrapMixin ],
  created() {
    const token = this.$store.getters.getToken
    if (token) this.$router.push('/profile')
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
        let title
        let message
        let variant
        switch (status) {
          case 400:
            title = "Form Error"
            message = "Invalid form inputs."
            variant = "warning"
            break
          case 401:
            title = "Form Error"
            message = "Invalid login credentials."
            variant = "warning"
            break
          default:
            title = "Server Error"
            message = "An unexpected server error has occurred. Please try again later."
            variant = "danger"
            break
        }
        this.appendToast(title, message, variant)
      },
      inputs : [
        {
          label: "Email",
          type: "email",
          key: "email",
          value: null,
          placeholder: "example@email.com",
          validator: (value) => {
            const regex = /^[\w!#$%&'*+/=?`{|}~^-]+(?:\.[\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z\d-]+\.)+[a-zA-Z]{2,6}$/
            return (value === null) ? null : regex.test(value)
          },
          validatorMessage: null
        },
        {
          label: "Password",
          type: "password",
          key: "password",
          value: null,
          placeholder: "",
          validator: (value) => {
            const regex = /^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?\d).{6,20}$/
            return (value === null) ? null : regex.test(value)
          },
          validatorMessage: null
        }
      ]
    }
  }
}
</script>
