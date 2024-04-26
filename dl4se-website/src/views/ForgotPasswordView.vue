<template>
  <div id="forgot-password">
    <h1 class="text-center">Request New Password</h1>
    <b-text-input-form v-model="inputs" :consumer="request" />
  </div>
</template>

<script>
import {email, required} from "@vuelidate/validators";
import BTextInputForm from "@/components/TextInputForm";
import routerMixin from "@/mixins/routerMixin";
import bootstrapMixin from "@/mixins/bootstrapMixin";

export default {
  components: { BTextInputForm },
  mixins: [routerMixin, bootstrapMixin],
  methods: {
    async request() {
      const payload = {};
      Object.entries(this.inputs).forEach(([key, data]) => (payload[key] = data.value));
      await this.$http
        .post("/user/password/forgotten", payload)
        .then(() => {
          this.redirectHomeAndToast(
            "Password Reset Requested",
            "We have initiated a password reset procedure for your account. Please check the specified email for further instructions.",
            "secondary",
          );
        })
        .catch((err) => {
          const status = err.response.status;
          const handler = this.errorHandlers[status];
          handler();
        });
    },
  },
  data() {
    return {
      errorHandlers: {
        0: () =>
          this.appendToast(
            "Server Error",
            "An unexpected server error has occurred. Please try again later.",
            "danger",
          ),
        400: () => this.appendToast("Form Error", "Invalid form inputs.", "warning"),
        404: () =>
          this.appendToast("Account Not Found", "No account is associated to the specified email address.", "warning"),
      },
      inputs: {
        email: {
          label: "Please specify your account email",
          type: "email",
          value: null,
          placeholder: "example@email.com",
          autocomplete: "email",
          feedback: false,
          rules: {
            $autoDirty: true,
            required: required,
            format: email,
          },
        },
      },
    };
  },
};
</script>
