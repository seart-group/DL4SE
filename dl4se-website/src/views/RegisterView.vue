<template>
  <div id="register">
    <h1 class="text-center">Register</h1>
    <b-container>
      <b-form @submit.prevent.stop="register" novalidate>
        <b-form-row>
          <b-form-group label-for="email" :state="valid('email')">
            <template #label>
              Email
              <b-icon-asterisk font-scale="0.35" shift-v="32" class="text-danger" />
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
            <template #description v-if="Boolean(v$.form.email.$invalid)">
              <b-form-text> Must be an OWASP-compliant email address. </b-form-text>
            </template>
          </b-form-group>
        </b-form-row>
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
          <b-form-group label-for="organisation" :state="valid('organisation')">
            <template #label>
              Organisation
              <b-icon-asterisk font-scale="0.35" shift-v="32" class="text-danger" />
            </template>
            <b-form-input
              id="organisation"
              name="organisation"
              type="text"
              v-model.trim="form.organisation"
              :disabled="submitted"
              :state="valid('organisation')"
              placeholder="Your Organisation Name"
            />
            <template #description v-if="!!v$.form.organisation.$invalid">
              <b-form-text>We use this information solely for analytics.</b-form-text>
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
import { email, required, sameAs } from "@vuelidate/validators";
import { password } from "@/validators";
import routerMixin from "@/mixins/routerMixin";
import bootstrapMixin from "@/mixins/bootstrapMixin";

export default {
  mixins: [routerMixin, bootstrapMixin],
  methods: {
    valid(key) {
      const element = this.v$.form[key];
      return element.$dirty ? !element.$invalid : null;
    },
    async register() {
      this.submitted = true;
      const payload = {
        email: this.form.email,
        password: this.form.password,
        organisation: this.form.organisation,
      };
      await this.$http
        .post("/user/register", payload)
        .then(() => {
          this.redirectHomeAndToast(
            "Account Created",
            "Your account has been created. We have sent you a verification link. Please check your email.",
            "secondary",
          );
        })
        .catch((err) => {
          switch (err.response.status) {
            case 400: {
              this.appendToast("Form Error", "Invalid form inputs.", "warning");
              break;
            }
            case 409: {
              this.appendToast("Form Error", "Email already in use.", "warning");
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
  beforeRouteEnter(_to, _from, next) {
    next((vm) => {
      const token = vm.$store.getters.getToken;
      if (token) vm.$router.replace({ name: "home" });
    });
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
        password: null,
        confirm: null,
        organisation: null,
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
        organisation: {
          $autoDirty: true,
          required: required,
        },
      },
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/view/register.sass" />
