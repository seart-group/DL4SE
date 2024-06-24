<template>
  <div id="register">
    <h1 class="text-center">Register</h1>
    <b-container>
      <b-form @submit.prevent.stop="register" novalidate>
        <b-form-row>
          <b-form-group label-for="email">
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
              :state="v$.form.email.$dirty ? !v$.form.email.$invalid : null"
              placeholder="example@email.com"
              autofocus
              @blur="autofillOrganisation"
            />
          </b-form-group>
        </b-form-row>
        <b-form-row>
          <b-form-group label-for="password">
            <template #label>
              Password
              <b-icon-asterisk font-scale="0.35" shift-v="16" class="text-danger" />
            </template>
            <b-form-input-password
              id="password"
              name="password"
              autocomplete="new-password"
              v-model.trim="form.password"
              :disabled="submitted"
              :state="v$.form.password.$dirty ? !v$.form.password.$invalid : null"
            />
            <b-form-text v-if="v$.form.password.$invalid">
              <b-icon-exclamation-octagon />
              <span>Must be at least 6 characters long, containing a number, uppercase and lowercase character.</span>
            </b-form-text>
          </b-form-group>
        </b-form-row>
        <b-form-row>
          <b-form-group label-for="confirm">
            <template #label>
              Confirm Password
              <b-icon-asterisk font-scale="0.35" shift-v="16" class="text-danger" />
            </template>
            <b-form-input-password
              id="confirm"
              name="confirm"
              autocomplete="new-password"
              v-model.trim="form.confirm"
              :disabled="submitted"
              :state="v$.form.confirm.$dirty ? !v$.form.confirm.$invalid : null"
            />
            <b-form-invalid-feedback v-if="v$.form.confirm.$dirty && v$.form.confirm.$invalid">
              <b-icon-exclamation-triangle />
              Must match the password above.
            </b-form-invalid-feedback>
          </b-form-group>
        </b-form-row>
        <b-form-row>
          <!-- FIXME: We have to do this manually because the inner group div can not be customized -->
          <div role="group" class="form-group">
            <label for="organisation" class="d-block">
              Organisation
              <b-icon-asterisk font-scale="0.35" shift-v="16" class="text-danger" />
            </label>
            <div class="position-relative">
              <b-form-auto-complete
                id="organisation"
                name="organisation"
                type="text"
                v-model.trim="form.organisation"
                :server="organisationsURL"
                query-param="name"
                :debounce-time="250"
                :server-params="{ size: 5 }"
                :response-mapper="responseMapper"
                :disabled="submitted"
                :state="v$.form.organisation.$dirty ? !v$.form.organisation.$invalid : null"
              />
            </div>
            <b-form-text v-if="v$.form.organisation.$invalid">
              <b-icon-exclamation-octagon />
              We use this information for analytics.
            </b-form-text>
          </div>
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
import { email, required, sameAs } from "@vuelidate/validators";
import { password } from "@/validators";
import routerMixin from "@/mixins/routerMixin";
import organisationsMixin from "@/mixins/organisationsMixin";
import bootstrapMixin from "@/mixins/bootstrapMixin";
import BFormAutoComplete from "@/components/FormAutoComplete";
import BFormInputPassword from "@/components/FormInputPassword";
import BFormSubmit from "@/components/FormSubmit";

export default {
  mixins: [routerMixin, organisationsMixin, bootstrapMixin],
  components: {
    BFormAutoComplete,
    BFormInputPassword,
    BFormSubmit,
  },
  methods: {
    responseMapper(json) {
      return json.map((item) => item.name);
    },
    async autofillOrganisation() {
      const validator = this.v$.form.email;
      const dirty = validator.$dirty;
      const valid = !validator.$invalid;
      if (!dirty || !valid) return;
      const email = validator.$model;
      const [, domain] = email.split("@");
      const organisation = await this.$http.get(`/organisation/${domain}`).then(({ data }) => data?.name);
      if (!organisation) return;
      this.form.organisation = organisation;
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
  head() {
    return {
      title: "Register",
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/view/register.sass" />
