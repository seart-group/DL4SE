<template>
  <b-form @submit.prevent.stop="postData" novalidate class="text-input-form">
    <b-form-row v-for="[key, data] in Object.entries(inputs)" :key="key">
      <b-form-group
          :id="key"
          :label="data.label"
          :label-for="'input-' + key"
          class="text-input-group"
      >
        <b-form-input
            :id="'input-' + key"
            :type="data.type"
            :placeholder="data.placeholder"
            :disabled="submitted"
            :state="data.validator(data.value)"
            v-model="data.value"
            class="text-input-field"
        />
        <b-form-invalid-feedback
            :state="data.validator(data.value)"
            v-if="data.feedback"
        >
          {{ data.feedback }}
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
    inputs: Object,
    apiTarget: String,
    successHandler: Function,
    failureHandler: Function
  },
  computed: {
    canSubmit() {
      return Object.values(this.inputs)
          .map(data => !!data.validator(data.value))
          .reduce((acc, curr) => acc && curr, true)
    }
  },
  methods: {
    async postData() {
      this.submitted = true

      const payload = {}
      Object.entries(this.inputs).forEach(([key, data]) => payload[key] = data.value)

      const config = {
        headers : {
          'content-type': 'application/json'
        }
      }

      await axios.post(this.apiTarget, payload, config)
          .then(this.successHandler)
          .catch(this.failureHandler)
    }
  },
  data() {
    return {
      submitted: false
    }
  }
}
</script>