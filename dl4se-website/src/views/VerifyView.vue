<template>
  <div id="verify">
    <h1 class="d-none">Account Verification</h1>
    <b-container>
      <b-form v-if="failed" @submit.prevent.stop="resendToken">
        <h2 class="text-center">Verification Link Expired</h2>
        <b-form-row class="justify-content-center">
          <b-form-group>
            <b-form-submit>
              <b-icon-arrow-clockwise shift-h="-2" rotate="45" />
              Resend
            </b-form-submit>
          </b-form-group>
        </b-form-row>
      </b-form>
      <b-row v-else>
        <b-col>
          <h2 class="text-center">Verifying Account</h2>
          <p class="text-center">This should only take a moment.</p>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import routerMixin from "@/mixins/routerMixin";
import BFormSubmit from "@/components/FormSubmit";

export default {
  mixins: [routerMixin],
  components: { BFormSubmit },
  props: {
    token: {
      type: String,
      required: true,
    },
  },
  methods: {
    async apiCall(endpoint, successHandler, errorHandler) {
      const config = { params: { token: this.token } };
      await this.$http.get(endpoint, config).then(successHandler).catch(errorHandler);
    },
    resendSuccess() {
      this.redirectHomeAndToast(
        "Token Resent",
        "We have sent you a new verification link. Please check your email.",
        "secondary",
      );
    },
    resendError() {
      this.redirectHomeAndToast(
        "Server Error",
        "An unexpected server error has occurred. Please try again later.",
        "danger",
      );
    },
    async resendToken() {
      await this.apiCall("/user/verify/resend", this.resendSuccess, this.resendError);
    },
    verifySuccess() {
      this.failed = false;
      this.redirectHomeAndToast("Account Verified", "Your account has been verified. You can now log in.", "secondary");
    },
    verifyError(err) {
      const status = err.response.status;
      switch (status) {
        case 403:
          break;
        case 404:
          this.redirectHomeAndToast(
            "Invalid Token",
            "The specified token does not exist. Check the link for errors and try again.",
            "warning",
          );
          break;
        default:
          this.redirectHomeAndToast(
            "Server Error",
            "An unexpected server error has occurred. Please try again later.",
            "danger",
          );
          break;
      }
    },
    async verifyToken() {
      await this.apiCall("/user/verify", this.verifySuccess, this.verifyError);
    },
  },
  async created() {
    await this.verifyToken();
  },
  data() {
    return { failed: true };
  },
};
</script>
