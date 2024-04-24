<template>
  <b-dialog-page
    id="verify"
    v-cloak
    v-if="showHtml"
    title="The verification link has expired."
    :actions="actions"
  />
</template>

<script>
import routerMixin from "@/mixins/routerMixin"
import BDialogPage from "@/components/DialogPage"

export default {
  props: {
    token: String,
  },
  components: { BDialogPage },
  mixins: [routerMixin],
  methods: {
    async apiCall(endpoint, successHandler, errorHandler) {
      const config = { params: { token: this.token } }
      await this.$http.get(endpoint, config).then(successHandler).catch(errorHandler)
    },
    async resendToken() {
      await this.apiCall(this.resendEndpoint, this.resendSuccess, this.resendError)
    },
    async verifyToken() {
      await this.apiCall(this.verifyEndpoint, this.verifySuccess, this.verifyError)
    },
  },
  async created() {
    await this.verifyToken()
  },
  data() {
    return {
      showHtml: false,
      verifyEndpoint: "/user/verify",
      resendEndpoint: "/user/verify/resend",
      actions: [
        {
          text: "Resend",
          icon: "arrow-clockwise",
          action: this.resendToken,
        },
      ],
      verifySuccess: () => {
        this.redirectHomeAndToast(
          "Account Verified",
          "Your account has been verified. You can now log in.",
          "secondary",
        )
      },
      verifyError: (err) => {
        const status = err.response.status
        let title
        let message
        let variant
        let action
        switch (status) {
          case 403:
            action = () => {
              this.showHtml = true
            }
            break
          case 404:
            title = "Invalid Token"
            message = "The specified token does not exist. Check the link for errors and try again."
            variant = "warning"
            action = () => {
              this.redirectHomeAndToast(title, message, variant)
            }
            break
          default:
            title = "Server Error"
            message = "An unexpected server error has occurred. Please try again later."
            variant = "danger"
            action = () => {
              this.redirectHomeAndToast(title, message, variant)
            }
            break
        }
        action()
      },
      resendSuccess: () => {
        this.redirectHomeAndToast(
          "Token Resent",
          "We have sent you a new verification link. Please check your email.",
          "secondary",
        )
      },
      resendError: () => {
        this.redirectHomeAndToast(
          "Server Error",
          "An unexpected server error has occurred. Please try again later.",
          "danger",
        )
      },
    }
  },
}
</script>
