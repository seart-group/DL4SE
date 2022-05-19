<template>
  <b-form @submit.prevent.stop="postData" novalidate class="m-5 text-center">
    <b-form-row
        v-for="(input, idx) in inputs"
        :key="input.key"
    >
      <b-form-group
          :id="input.key"
          :label="input.label"
          :label-for="'input-' + idx"
          class="text-input-group"
      >
        <b-form-input
            :id="'input-' + idx"
            :type="input.type"
            :placeholder="input.placeholder"
            :disabled="submitted"
            :state="input.validator(input.value)"
            v-model="input.value"
            class="text-input-field"
        />
        <b-form-invalid-feedback
            :state="input.validator(input.value)"
            v-if="input.validatorMessage"
        >
          {{ input.validatorMessage }}
        </b-form-invalid-feedback>
      </b-form-group>
    </b-form-row>
    <b-button type="submit" :disabled="!canSubmit || submitted" class="action-btn">
      Submit
    </b-button>
  </b-form>
</template>

<script>
import axios from "axios"

export default {
  name: "text-input-form",
  props: {
    apiTarget: String,
    inputs: Array[Object],
    successHandler: Function,
    failureHandler: Function
  },
  computed: {
    canSubmit: function () {
      return this.inputs.map(input => !!input.validator(input.value)).reduce((acc, curr) => acc && curr, true)
    }
  },
  methods: {
    async postData() {
      this.submitted = true

      const data = {}
      this.inputs.forEach((input) => {
        data[input.key] = input.value
      })

      const config = {
        headers : {
          'content-type': 'application/json'
        }
      }

      await axios.post(this.apiTarget, data, config)
          .then(this.successHandler)
          .catch(this.failureHandler)

      this.submitted = false
    }
  },
  data() {
    return {
      submitted: false
    }
  }
}
</script>