<template>
  <div id="login">
    <h1 class="text-center">Log In</h1>
    <b-container>
      <b-form @submit.prevent.stop="login" novalidate>
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
            />
          </b-form-group>
        </b-form-row>
        <b-form-row>
          <b-form-group label-for="password">
            <template #label>
              Password
              <b-icon-asterisk font-scale="0.35" shift-v="16" class="text-danger" />
            </template>
            <b-form-input
              id="password"
              name="password"
              type="password"
              autocomplete="current-password"
              v-model.trim="form.password"
              :disabled="submitted"
              :state="v$.form.password.$dirty ? !v$.form.password.$invalid : null"
            />
          </b-form-group>
        </b-form-row>
        <b-form-row>
          <b-form-group>
            <b-form-text text-variant="dark" class="text-left">
              <b-icon-asterisk font-scale="0.35" class="text-danger" />
              Required fields
            </b-form-text>
          </b-form-group>
        </b-form-row>
        <b-form-row>
          <b-form-group>
            <b-link :to="{ name: 'forgot' }">Forgotten password?</b-link>
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
import { password } from "@/validators";
import bootstrapMixin from "@/mixins/bootstrapMixin";
import BFormSubmit from "@/components/FormSubmit";

export default {
  mixins: [bootstrapMixin],
  components: { BFormSubmit },
  computed: {
    target() {
      return this.$route.query.target || "home";
    },
  },
  methods: {
    async login() {
      this.submitted = true;
      await this.$http
        .post("/user/login", this.form)
        .then(({ data: token }) => {
          this.$store.commit("setToken", token);
          this.$router.replace({ name: this.target });
        })
        .catch((err) => {
          switch (err.response.status) {
            case 400: {
              this.appendToast("Form Error", "Invalid form inputs.", "warning");
              break;
            }
            case 401: {
              this.appendToast("Form Error", "Invalid login credentials.", "warning");
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
      if (token) vm.$router.replace({ name: vm.target });
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
      },
    };
  },
  head() {
    return {
      title: "Log In",
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/view/login.sass" />
