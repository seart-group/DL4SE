<template>
  <div id="register">
    <h1 class="page-title">Register</h1>
    <text-input-form
        v-model="inputs"
        :api-target="registerTarget"
        :success-handler="registerSuccess"
        :failure-handler="registerFailure"
    />
  </div>
</template>

<script>
import {email, helpers, required} from "@vuelidate/validators";
import bootstrapMixin from '@/mixins/bootstrapMixin'
import TextInputForm from '@/components/TextInputForm';

export default {
  components: {
    TextInputForm
  },
  mixins: [ bootstrapMixin ],
  data () {
    return {
      registerTarget: "https://localhost:8080/api/user/register",
      registerSuccess: () => {
        this.$router.push('/').then(() => {
          this.appendToast(
              "Account Created",
              "Your account has been created. We have sent you a verification link. Please check your email.",
              "secondary"
          )
        })
      },
      registerFailure: (err) => {
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
      inputs: {
        email: {
          label: "Email",
          type: "email",
          value: null,
          placeholder: "example@email.com",
          feedback: true,
          rules: {
            $autoDirty: true,
            ruleRequired: required,
            ruleEmail: email
          }
        },
        password: {
          label: "Password",
          type: "password",
          value: null,
          placeholder: "",
          feedback: true,
          rules: {
            $autoDirty: true,
            ruleRequired: required,
            rulePassword: helpers.regex(/^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?\d).{6,20}$/)
          }
        },
        organisation: {
          label: "Organisation",
          type: "text",
          value: null,
          placeholder: "",
          feedback: true,
          rules: {
            $autoDirty: true,
            ruleRequired: required
          }
        }
      }
    }
  }
}
</script>