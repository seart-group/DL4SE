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
      checkTarget: "https://localhost:8080/api/user",
      checkSuccess: (response) => {
        const uid = response.data.uid
        this.$router.push('/profile/' + uid)
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
        let title
        let message
        let variant
        switch (status) {
          case 400:
            title = "Form Error"
            message = "Invalid form inputs."
            variant = "warning"
            break
          case 401:
            title = "Form Error"
            message = "Invalid login credentials."
            variant = "warning"
            break
          default:
            title = "Server Error"
            message = "An unexpected server error has occurred. Please try again later."
            variant = "danger"
            break
        }
        this.appendToast(title, message, variant)
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
