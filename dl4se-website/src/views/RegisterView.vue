<template>
  <div id="register">
    <h1 class="page-title">Register</h1>
    <b-text-input-form v-model="inputs" :consumer="register" />
  </div>
</template>

<script>
import {email, helpers, required} from "@vuelidate/validators"
import routerMixin from "@/mixins/routerMixin"
import bootstrapMixin from '@/mixins/bootstrapMixin'
import BTextInputForm from '@/components/TextInputForm'

export default {
  components: { BTextInputForm },
  mixins: [ routerMixin, bootstrapMixin ],
  methods: {
    async register() {
      const payload = {}
      Object.entries(this.inputs).forEach(([key, data]) => payload[key] = data.value)
      await this.$http.post("/user/register", payload)
          .then(() => {
            this.redirectHomeAndToast(
                "Account Created",
                "Your account has been created. We have sent you a verification link. Please check your email.",
                "secondary"
            )
          })
          .catch((err) => {
            const status = err.response.status
            const handler = this.errorHandlers[status]
            handler()
          })
    }
  },
  data () {
    return {
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
        409: () => this.appendToast(
            "Form Error",
            "Email already in use.",
            "warning"
        )
      },
      inputs: {
        email: {
          label: "Email",
          type: "email",
          value: null,
          placeholder: "example@email.com",
          autocomplete: "email",
          feedback: true,
          rules: {
            $autoDirty: true,
            required: helpers.withMessage("", required),
            format: helpers.withMessage("Invalid email address", email)
          }
        },
        password: {
          label: "Password",
          type: "password",
          value: null,
          autocomplete: "new-password",
          feedback: true,
          rules: {
            $autoDirty: true,
            required: helpers.withMessage("", required),
            format: helpers.withMessage(
                "Password must be between 6 and 20 characters, and contain one uppercase letter and number.",
                helpers.regex(/^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?\d).{6,20}$/)
            )
          }
        },
        organisation: {
          label: "Organisation",
          type: "text",
          value: null,
          autocomplete: "organization",
          feedback: true,
          rules: {
            $autoDirty: true,
            required: helpers.withMessage("", required)
          }
        }
      }
    }
  }
}
</script>