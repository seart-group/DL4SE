<template>
  <div id="reset-password">
    <h1 class="text-center">Specify New Password</h1>
    <b-container>
      <b-form @submit.prevent.stop="reset" novalidate>
        <b-form-row>
          <b-form-group label-for="password" :state="valid('password')">
            <template #label>
              Password
              <b-icon-asterisk font-scale="0.35" shift-v="32" class="text-danger" />
            </template>
            <b-form-input
              id="password"
              name="password"
              type="password"
              autocomplete="new-password"
              v-model.trim="form.password"
              :disabled="submitted"
              :state="valid('password')"
            />
            <template #description v-if="Boolean(v$.form.password.$invalid)">
              <b-form-text>
                Must be at least 6 characters long, containing one uppercase letter and a number.
              </b-form-text>
            </template>
          </b-form-group>
        </b-form-row>
        <b-form-row>
          <b-form-group label-for="confirm" :state="valid('confirm')">
            <template #label>
              Confirm Password
              <b-icon-asterisk font-scale="0.35" shift-v="32" class="text-danger" />
            </template>
            <b-form-input
              id="confirm"
              name="confirm"
              type="password"
              autocomplete="new-password"
              v-model.trim="form.confirm"
              :disabled="submitted"
              :state="valid('confirm')"
            />
            <template #invalid-feedback>
              <b-form-text> Must match the password above. </b-form-text>
            </template>
          </b-form-group>
        </b-form-row>
        <b-form-row>
          <b-form-group>
            <b-form-text class="text-left">
              <b-icon-asterisk font-scale="0.35" shift-v="32" class="text-danger" />
              Required fields
            </b-form-text>
          </b-form-group>
        </b-form-row>
        <b-form-row>
          <b-form-group>
            <b-button type="submit" variant="secondary" :disabled="v$.$invalid || submitted">Submit</b-button>
          </b-form-group>
        </b-form-row>
        <b-overlay :show="submitted" variant="light" no-wrap :z-index="Number.MAX_SAFE_INTEGER" />
      </b-form>
    </b-container>
  </div>
</template>

<script>
import useVuelidate from "@vuelidate/core";
import { required, sameAs } from "@vuelidate/validators";
import { password } from "@/validators";
import routerMixin from "@/mixins/routerMixin";
import bootstrapMixin from "@/mixins/bootstrapMixin";

export default {
  mixins: [routerMixin, bootstrapMixin],
  props: {
    token: String,
  },
  methods: {
    valid(key) {
      const element = this.v$.form[key];
      return element.$dirty ? !element.$invalid : null;
    },
    async reset() {
      this.submitted = true;
      const payload = { password: this.form.password };
      const config = { params: { token: this.token } };
      await this.$http
        .post("/user/password/reset", payload, config)
        .then(() => {
          this.redirectHomeAndToast(
            "Password Reset Successful",
            "Your password has been reset successfully. You can now log into your account with the new password.",
            "secondary",
          );
        })
        .catch((err) => {
          switch (err.response.status) {
            case 400: {
              this.appendToast("Form Error", "Invalid form inputs.", "warning");
              break;
            }
            case 403: {
              this.redirectHomeAndToast(
                "Token Expired",
                "The received token has expired. Please restart the password recovery process.",
                "warning",
              );
              break;
            }
            case 404: {
              this.redirectHomeAndToast(
                "Invalid Token",
                "The specified token does not exist. Check the link for errors and try again.",
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
        password: null,
        confirm: null,
      },
    };
  },
  validations() {
    return {
      form: {
        password: {
          $autoDirty: true,
          required: required,
          format: password,
        },
        confirm: {
          $autoDirty: true,
          required: required,
          format: password,
          sameAs: sameAs(this.form.password),
        },
      },
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/view/reset-password.sass" />
