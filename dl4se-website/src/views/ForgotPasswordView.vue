<template>
  <div id="forgot-password">
    <h1 class="text-center">Request New Password</h1>
    <b-container>
      <b-form @submit.prevent.stop="request" novalidate>
        <b-form-row>
          <b-form-group label-for="email" :state="valid('email')">
            <template #label>
              Email
              <b-icon-asterisk font-scale="0.35" shift-v="16" class="text-danger" />
            </template>
            <b-form-input
              id="email"
              name="email"
              type="email"
              autocomplete="email"
              v-model.trim="form.email"
              :disabled="submitted"
              :state="valid('email')"
              placeholder="example@email.com"
              autofocus
            />
          </b-form-group>
        </b-form-row>
        <b-form-row>
          <b-form-group>
            <b-form-text text-variant="dark">
              <b-icon-asterisk font-scale="0.35" class="text-danger" />
              Required fields
            </b-form-text>
          </b-form-group>
        </b-form-row>
        <b-form-row>
          <b-form-group>
            <b-form-submit :disabled="v$.$invalid || submitted" />
          </b-form-group>
        </b-form-row>
        <b-overlay :show="submitted" variant="light" no-wrap :z-index="Number.MAX_SAFE_INTEGER" />
      </b-form>
    </b-container>
  </div>
</template>

<script>
import useVuelidate from "@vuelidate/core";
import { email, required } from "@vuelidate/validators";
import routerMixin from "@/mixins/routerMixin";
import bootstrapMixin from "@/mixins/bootstrapMixin";
import BFormSubmit from "@/components/FormSubmit";

export default {
  mixins: [routerMixin, bootstrapMixin],
  components: { BFormSubmit },
  methods: {
    valid(key) {
      const element = this.v$.form[key];
      return element.$dirty ? !element.$invalid : null;
    },
    async request() {
      this.submitted = true;
      await this.$http
        .post("/user/password/forgotten", this.form)
        .then(() => {
          this.redirectHomeAndToast(
            "Password Reset Requested",
            `We have initiated a password reset procedure for your account.
             Please check the specified email for further instructions.`,
            "secondary",
          );
        })
        .catch((err) => {
          switch (err.response.status) {
            case 400: {
              this.appendToast("Form Error", "Invalid form inputs.", "warning");
              break;
            }
            case 404: {
              this.appendToast(
                "Account Not Found",
                "No account is associated to the specified email address.",
                "warning",
              );
              break;
            }
            default: {
              this.appendToast(
                "Server Error",
                "An unexpected server error has occurred. Please try again later.",
                "danger",
              );
            }
          }
        })
        .finally(() => {
          this.submitted = false;
        });
    },
  },
  setup() {
    return {
      v$: useVuelidate(),
    };
  },
  data() {
    return {
      submitted: false,
      form: {
        email: null,
      },
    };
  },
  validations() {
    return {
      form: {
        email: {
          $autoDirty: true,
          required: required,
          format: email,
        },
      },
    };
  },
  head() {
    return {
      title: "Forgot Password",
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/view/forgot-password.sass" />
