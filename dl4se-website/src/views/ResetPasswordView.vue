<template>
  <div id="reset-password">
    <h1 class="page-title">Specify New Password</h1>
    <b-text-input-form v-model="inputs" :consumer="reset" />
  </div>
</template>

<script>
import BTextInputForm from "@/components/TextInputForm";
import routerMixin from "@/mixins/routerMixin";
import bootstrapMixin from "@/mixins/bootstrapMixin";
import {helpers, required} from "@vuelidate/validators";

export default {
  name: "PasswordResetView",
  components: { BTextInputForm },
  mixins: [ routerMixin, bootstrapMixin ],
  props: {
    token: String
  },
  methods: {
    async reset() {
      const payload = {}
      Object.entries(this.inputs).forEach(([key, data]) => payload[key] = data.value)
      await this.$http.post("/user/password/reset", payload, { params: { token: this.token } })
          .then(() => {
            this.redirectHomeAndToast(
                "Password Reset Successful",
                "Your password has been reset successfully. You can now log into your account with the new password.",
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
  data() {
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
        403: () => this.redirectHomeAndToast(
            "Token Expired",
            "The received token has expired. Please restart the password recovery process.",
            "warning"
        ),
        404: () => this.redirectHomeAndToast(
            "Invalid Token",
            "The specified token does not exist. Check the link for errors and try again.",
            "warning"
        )
      },
      inputs : {
        password: {
          label: "Type In Your New Password",
          type: "password",
          value: null,
          autocomplete: "new-password",
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