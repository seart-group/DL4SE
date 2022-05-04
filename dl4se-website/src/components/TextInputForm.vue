<template>
  <form @submit.prevent.stop>
    <div v-for="input in inputs" :key="input.key">
      <label>{{ input.label }}</label>
      <sup v-if="input.required">*</sup>
      <input
          :type="input.type"
          :required="input.required"
          :placeholder="input.placeholder"
          v-model="input.value"
      />
    </div>
    <button @click="postData" type="submit">Submit</button>
  </form>
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

<style scoped>

form {
  width: 80%;
  margin: 0 auto;
}

label {
  margin-top: 10px;
  margin-bottom: 10px;
  width: 10%;
  text-align: right;
  display: block;
}

input {
  margin: 10px;
}

label, input {
  display: inline-block;
}

sup {
  color: #721c24;
}
</style>