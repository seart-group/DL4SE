<template>
  <div id="login">
    <h1 class="page-title">Log In</h1>
    <b-text-input-form v-model="inputs" :consumer="login" />
  </div>
</template>

<script>
import {email, helpers, required} from "@vuelidate/validators"
import bootstrapMixin from '@/mixins/bootstrapMixin'
import BTextInputForm from '@/components/TextInputForm'

export default {
  components: { BTextInputForm },
  mixins: [ bootstrapMixin ],
  methods: {
    async login() {
      const payload = {}
      Object.entries(this.inputs).forEach(([key, data]) => payload[key] = data.value)
      const config = { headers : { 'content-type': 'application/json' }}

      await this.$http.post("/user/login", payload, config)
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
          placeholder: "",
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
