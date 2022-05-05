<template>
  <b-form @submit.prevent.stop class="m-4">
    <b-form-row v-for="(input, idx) in inputs" :key="input.key">
      <b-form-group
          :id="input.key"
          :label="input.label"
          :label-for="'input-' + idx"
          class="mx-auto text-left"
      >
        <b-form-input
            :id="'input-' + idx"
            :type="input.type"
            :required="input.required"
            :placeholder="input.placeholder"
            v-model="input.value"
        />
      </b-form-group>
    </b-form-row>
    <b-button @click="postData" type="submit">Submit</b-button>
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
    postData() {
      const data = {}
      this.inputs.forEach((input) => {
        data[input.key] = input.value
      })

      const config = {
        headers : {
          'content-type': 'application/json'
        }
      }

      axios.post(this.apiTarget, data, config)
          .then(this.successHandler)
          .catch(this.failureHandler)
    }
  }
}
</script>