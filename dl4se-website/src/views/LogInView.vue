<template>
  <div id="login">
    <h1 class="page-title">Log In</h1>
    <b-text-input-form v-model="inputs" :consumer="login">
      <template #footer>
        <b-form-row>
          <b-form-group class="text-input-group-center">
            <b-link :to=" { name: 'forgot' } " class="text-secondary">Forgotten password?</b-link>
          </b-form-group>
        </b-form-row>
      </template>
    </b-text-input-form>
  </div>
</template>

<script>
import {email, helpers, required} from "@vuelidate/validators"
import bootstrapMixin from '@/mixins/bootstrapMixin'
import BTextInputForm from '@/components/TextInputForm'

export default {
  components: { BTextInputForm },
  mixins: [ bootstrapMixin ],
  computed: {
    target() {
      return this.$route.query.target || "home"
    }
  },
  methods: {
    async login() {
      const payload = {}
      Object.entries(this.inputs).forEach(([key, data]) => payload[key] = data.value)
      await this.$http.post("/user/login", payload)
          .then((response) => {
            const token = response.data
            this.$store.commit("setToken", token)
            this.$router.push({ name: "dashboard" })
          })
          .catch((err) => {
            const status = err.response.status
            const handler = this.errorHandlers[status]
            handler()
          })
    }
  },
  beforeRouteEnter(_to, _from, next) {
    next(vm => {
      const token = vm.$store.getters.getToken
      if (token) vm.$router.replace({ name: vm.target })
    })
  },
  data () {
    return {
      errorHandlers: {
        0: () => this.appendToast(
            "Server Error",
            "An unexpected server error has occurred. Please try again later.",
            "danger"
        ),
        400: () => this.appendToast(
            "Form Error",
            "Invalid form inputs.",
            "warning"
        ),
        401: () => this.appendToast(
            "Form Error",
            "Invalid login credentials.",
            "warning"
        )
      },
      inputs : {
        email: {
          label: "Email",
          type: "email",
          value: null,
          placeholder: "example@email.com",
          autocomplete: "email",
          feedback: false,
          rules: {
            $autoDirty: true,
            required: required,
            format: email
          }
        },
        password: {
          label: "Password",
          type: "password",
          value: null,
          autocomplete: "current-password",
          feedback: false,
          rules: {
            $autoDirty: true,
            required: required,
            format: helpers.regex(/^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?\d).{6,20}$/)
          }
        }
      }
    }
  }
}
</script>
