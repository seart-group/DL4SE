<template>
  <div id="register">
    <h1 class="page-title">Register</h1>
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
      apiTarget: "https://localhost:8080/api/user/register",
      successHandler: () => {
        this.inputs.forEach((input) => { input.value = "" })
        this.$router.push('/').then(() => {
          this.appendToast(
              "Account Created",
              "Your account has been created. We have sent you a verification link. Please check your email.",
              "secondary"
          )
        })
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
          case 409:
            title = "Form Error"
            message = "Email already in use."
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
          validatorMessage: "Please provide a valid email address."
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
          validatorMessage: "Password must be 6 to 20 characters long, and contain one uppercase letter and number."
        },
        {
          label: "Organisation",
          type: "text",
          key: "organisation",
          value: null,
          placeholder: "",
          validator: (value) => {
            const regex = /^[^\s-_][\w\s-]*$/
            return (value === null) ? null : regex.test(value)
          },
          validatorMessage: "This is a required field. Don't leave it blank!"
        }
      ]
    }
  }
}
</script>