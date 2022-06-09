<template>
  <b-dialog-page
      id="verify" v-cloak v-if="showHtml"
      title="The verification link has expired."
      :actions="actions"
  />
</template>

<script>
import axios from "axios"
import axiosMixin from "@/mixins/axiosMixin";
import BDialogPage from "@/components/DialogPage";

export default {
  props: {
    token: String
  },
  components: { BDialogPage },
  mixins: [ axiosMixin ],
  async created() {
    await this.apiCall(this.verifyLink, this.verifySuccess, this.verifyError)
  },
  methods: {
    async apiCall(link, successHandler, errorHandler) {
      const config = {
        params: {
          token: this.token
        }
      }

      await axios.get(link, config).then(successHandler).catch(errorHandler)
    },
    async resendToken() {
      this.blockInput = true
      await this.apiCall(this.resendLink, this.resendSuccess, this.resendError)
    }
  },
  computed: {
    resendLink: function () {
      return this.verifyLink + "/resend"
    }
  },
  data() {
    return {
      showHtml: false,
      blockInput: false,
      verifyLink: "https://localhost:8080/api/user/verify",
      actions: [
        {
          text: "Resend",
          icon: "arrow-clockwise",
          action: this.resendToken
        },
        {
          text: "Home",
          icon: "house-door",
          action: () => this.$router.push("/")
        }
      ],
      verifySuccess: () => {
        this.redirectHomeAndToast(
            "Account Verified",
            "Your account has been verified. You can now log in.",
            "secondary"
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
            action = () => { this.showHtml = true }
            break
          case 404:
            title = "Invalid Token"
            message = "The specified token does not exist. Check the link for errors and try again."
            variant = "warning"
            action = () => { this.redirectHomeAndToast(title, message, variant) }
            break
          default:
            title = "Server Error"
            message = "An unexpected server error has occurred. Please try again later."
            variant = "danger"
            action = () => { this.redirectHomeAndToast(title, message, variant) }
            break
        }
        action()
      },
      resendSuccess: () => {
        this.redirectHomeAndToast(
            "Token Resent",
            "We have sent you a new verification link. Please check your email.",
            "secondary"
        )
      },
      resendError: () => {
        this.redirectHomeAndToast(
            "Server Error",
            "An unexpected server error has occurred. Please try again later.",
            "danger"
        )
      }
    }
  }
}
</script>