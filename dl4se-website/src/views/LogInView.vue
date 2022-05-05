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
import bootstrapMixin from '@/mixins/bootstrapMixin'

export default {
  components: {
    TextInputForm
  },
  mixins: [ bootstrapMixin ],
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
        const title = "Error"
        const variant = "danger"
        let message
        switch (status) {
          case 400:
            message = "Invalid form inputs."
            break
          case 401:
            message = "Invalid login credentials."
            break
          default:
            message = "An unexpected server error has occurred. Please try again later."
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
