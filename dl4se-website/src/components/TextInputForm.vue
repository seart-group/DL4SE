<template>
  <form @submit.prevent.stop>
    <div v-for="input in inputs" :key="input.key">
      <label>{{ input.label }}</label>
      <input
          :type="input.type"
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
    inputs: Array[Object]
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
          .then((response) => {
            const token = response.data
            console.log(token)
            // TODO 04.05.22: Set the token value in the store
            // TODO 04.05.22: Re-route to user profile
            // TODO 04.05.22: Clear Input Values
          })
          .catch((err) => {
            console.log(err)
          })
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
  margin: 10px;
  width: 10%;
  text-align: right;
  display: block;
}

label,
input {
  display: inline-block;
}
</style>