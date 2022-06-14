<template>
  <div id="login">
    <h1 class="page-title">Log In</h1>
    <text-input-form
        v-model="inputs"
        :api-target="loginTarget"
        :success-handler="loginSuccess"
        :failure-handler="loginFailure"
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
  props: {
    showLoggedOut: {
      type: Boolean,
      default: false
    }
  },
  created() {
    if (this.showLoggedOut) {
      this.appendToast(
          "Logged Out",
          "Your session has expired. Please log in again.",
          "secondary"
      )
    }
  },
  data () {
    return {
      showHtml: false,
      errorHandlers: {
        0: () => this.appendToast(
            "Server Error",
            "An unexpected server error has occurred. Please try again later.",
            "danger"
        ),
        400: () => this.appendToast(
            "Form Error",
            "Invalid form inputs.",
            "warning"
        ),
        401: () => this.appendToast(
            "Form Error",
            "Invalid login credentials.",
            "warning"
        )
      },
      loginTarget: "https://localhost:8080/api/user/login",
      loginSuccess: (response) => {
        const token = response.data
        this.$store.commit("setToken", token)
        this.$router.push({ name: "dashboard" })
      },
      loginFailure: (err) => {
        const status = err.response.status
        const handler = this.errorHandlers[status]
        handler()
      },
      inputs : {
        email: {
          label: "Email",
          type: "email",
          value: null,
          placeholder: "example@email.com",
          feedback: false,
          rules: {
            $autoDirty: true,
            required: required,
            format: email
          }
        },
        password: {
          label: "Password",
          type: "password",
          value: null,
          placeholder: "",
          feedback: false,
          rules: {
            $autoDirty: true,
            required: required,
            format: helpers.regex(/^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?\d).{6,20}$/)
          }
        }
      }
    }
  }
}
</script>
