<template>
  <div id="verify" v-cloak v-if="showHtml">
    <h1 class="page-title">Link Expired</h1>
    <div class="m-5">
      <div class="row my-3">
        <h3 class="col text-center">
          The verification link has expired.
        </h3>
      </div>
      <div class="row my-3">
        <div class="col-6 text-right">
          <b-button :disabled="blockInput" @click="resendToken" class="action-btn">
            <b-icon-arrow-clockwise /> Resend
          </b-button>
        </div>
        <div class="col-6 text-left">
          <b-button :disabled="blockInput" to="/" class="action-btn">
            <b-icon-house-door /> Home
          </b-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios"
import bootstrapMixin from '@/mixins/bootstrapMixin'

export default {
  props: {
    token: String
  },
  mixins: [ bootstrapMixin ],
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
      verifySuccess: () => {
        this.returnHomeAndToast(
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
            action = () => { this.returnHomeAndToast(title, message, variant) }
            break
          default:
            title = "Server Error"
            message = "An unexpected server error has occurred. Please try again later."
            variant = "danger"
            action = () => { this.returnHomeAndToast(title, message, variant) }
            break
        }
        action()
      },
      resendSuccess: () => {
        this.returnHomeAndToast(
            "Token Resent",
            "We have sent you a new verification link. Please check your email.",
            "secondary"
        )
      },
      resendError: () => {
        this.returnHomeAndToast(
            "Server Error",
            "An unexpected server error has occurred. Please try again later.",
            "danger"
        )
      }
    }
  }
}
</script>