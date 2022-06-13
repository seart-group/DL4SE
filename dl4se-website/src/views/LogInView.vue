<template>
  <div id="login" v-cloak v-if="showHtml">
    <h1 class="page-title">Log In</h1>
    <text-input-form
        v-model="inputs"
        :api-target="loginTarget"
        :success-handler="loginSuccess"
        :failure-handler="loginFailure"
    />
  </div>
</template>

<script>
import axios from "axios"
import {email, helpers, required} from "@vuelidate/validators";
import bootstrapMixin from '@/mixins/bootstrapMixin'
import TextInputForm from '@/components/TextInputForm';

export default {
  components: {
    TextInputForm
  },
  mixins: [ bootstrapMixin ],
  methods: {
    async getUserDetails() {
      const token = this.$store.getters.getToken

      if (!token) {
        this.showHtml = true
        return
      }

      const config = {
        headers : {
          'authorization': token
        }
      }
      await axios.get(this.checkTarget, config).then(this.checkSuccess).catch(this.checkFailure)
    }
  },
  created() {
    this.getUserDetails()
  },
  data () {
    return {
      showHtml: false,
      errorHandlers: {
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
      fallbackErrorHandler: () => this.appendToast(
          "Server Error",
          "An unexpected server error has occurred. Please try again later.",
          "danger"
      ),
      checkTarget: "https://localhost:8080/api/user",
      checkSuccess: () => {
        this.$router.push({ name: "dashboard" })
      },
      checkFailure: () => {
        this.$store.commit("clearToken")
        this.showHtml = true
      },
      loginTarget: "https://localhost:8080/api/user/login",
      loginSuccess: (response) => {
        const token = response.data
        this.$store.commit("setToken", token)
        this.getUserDetails()
      },
      loginFailure: (err) => {
        const status = err.response.status
        const handler = this.errorHandlers[status] || this.fallbackErrorHandler
        handler()
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
