<template>
  <b-form @submit.prevent.stop="postData" novalidate class="m-4">
    <b-form-row
        v-for="(input, idx) in inputs"
        :key="input.key"
    >
      <b-form-group
          :id="input.key"
          :label="input.label"
          :label-for="'input-' + idx"
          class="mx-auto text-left w-25"
      >
        <b-form-input
            :id="'input-' + idx"
            :type="input.type"
            :placeholder="input.placeholder"
            :disabled="submitted"
            :state="input.validator(input.value)"
            v-model="input.value"
        />
        <b-form-invalid-feedback
            :state="input.validator(input.value)"
            v-if="input.validatorMessage"
        >
          {{ input.validatorMessage }}
        </b-form-invalid-feedback>
      </b-form-group>
    </b-form-row>
    <b-button :disabled="submitted" type="submit">Submit</b-button>
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