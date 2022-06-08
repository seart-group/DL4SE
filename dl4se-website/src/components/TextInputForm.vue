<template>
  <b-form @submit.prevent.stop="postData" novalidate class="text-input-form">
    <b-form-row v-for="[key, data] in Object.entries(inputs)" :key="key">
      <b-form-group :id="'label-'+key" class="text-input-group"
                    :label-for="'input-' + key" :state="entryState(key)"
      >
        <template #label>
          {{ data.label }}
          <b-icon-asterisk v-if="entryRequired(key)" font-scale="0.35" shift-v="32" class="text-input-icon" />
        </template>
        <b-form-input :id="'input-' + key" :type="data.type" class="text-input-field"
                      :state="entryState(key)" :disabled="submitted"
                      :placeholder="data.placeholder" v-model.trim="data.value"
        />
        <template #invalid-feedback v-if="entryFeedback(key)">
          <ul class="text-input-feedback">
            <li v-for="(error, idx) in entryErrors(key)" :key="idx">{{ error }}</li>
          </ul>
        </template>
      </b-form-group>
    </b-form-row>
    <b-form-row v-if="anyRequired">
      <b-form-group class="text-input-group">
        <template #description>
          <b-icon-asterisk font-scale="0.35" shift-v="32" class="text-input-icon" />
          Required fields
        </template>
      </b-form-group>
    </b-form-row>
    <b-button type="submit" :disabled="submitDisabled" class="action-btn">
      Submit
    </b-button>
  </b-form>
</template>

<script>
import axios from "axios"
import useVuelidate from "@vuelidate/core";
import {required} from "@vuelidate/validators";

export default {
  name: "text-input-form",
  props: {
    value: Object,
    apiTarget: String,
    successHandler: Function,
    failureHandler: Function
  },
  computed: {
    anyRequired() {
      return Object.values(this.inputs).map(input => {
        const inputRules = Object.values(input.rules)
        return inputRules.includes(required)
      }).reduce((curr, acc) => curr || acc, false)
    },
    submitDisabled() {
      return this.v$.$invalid || this.submitted
    }
  },
  methods: {
    entryDirty(key) {
      return this.v$.inputs[key].$dirty
    },
    entryValid(key) {
      return !this.v$.inputs[key].$invalid
    },
    entryRequired(key) {
      return Object.values(this.inputs[key].rules).includes(required)
    },
    entryState(key) {
      return this.entryDirty(key) ? this.entryValid(key) : null
    },
    entryErrors(key) {
      return this.v$.inputs[key].$errors.map(error => error.$message).filter(message => message)
    },
    entryFeedback(key) {
      return this.inputs[key].feedback && !!this.entryErrors(key).length
    },
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
  watch: {
    "inputs": {
      nested: true,
      handler() {
        this.$emit("input", this.inputs)
      }
    }
  },
  setup() {
    return {
      v$: useVuelidate()
    }
  },
  data() {
    return {
      submitted: false,
      inputs: this.value
    }
  },
  validations() {
    const validations = { inputs: {} }
    Object.entries(this.inputs).forEach(([key, data]) => {
      validations.inputs[key] = { value: data.rules }
    })

    return validations
  }
}
</script>