<template>
  <b-form @submit.prevent.stop="submit" novalidate class="text-input-form">
    <slot name="header" />
    <b-form-row v-for="([key, data], idx) in Object.entries(inputs)" :key="key">
      <b-form-group :id="`label-${key}`" class="text-input-group-left"
                    :label-for="`input-${key}`" :state="entryState(key)"
      >
        <template #label v-if="data.label">
          {{ data.label }}
          <b-icon-asterisk v-if="entryRequired(key)" font-scale="0.35" shift-v="32" class="text-input-icon" />
        </template>
        <b-form-input :id="`input-${key}`" :name="key" :type="data.type" class="text-input-field"
                      :state="entryState(key)" :disabled="submitted"
                      :autofocus="!idx" :autocomplete="data.autocomplete"
                      :placeholder="data.placeholder" v-model.trim="data.value"
        />
        <template #invalid-feedback v-if="entryFeedback(key)">
          <ul class="text-input-feedback">
            <li v-for="(error, idx) in entryErrors(key)" :key="idx">{{ error }}</li>
          </ul>
        </template>
      </b-form-group>
    </b-form-row>
    <b-form-row v-if="displayRequired">
      <b-form-group class="text-input-group-left">
        <template #description>
          <b-icon-asterisk font-scale="0.35" shift-v="32" class="text-input-icon" />
          Required fields
        </template>
      </b-form-group>
    </b-form-row>
    <b-form-row>
      <b-form-group class="text-input-group-center">
        <b-button type="submit" :disabled="submitDisabled" class="action-btn">
          Submit
        </b-button>
      </b-form-group>
    </b-form-row>
    <slot name="footer" />
    <b-overlay :show="submitted" variant="light" no-wrap :z-index="Number.MAX_SAFE_INTEGER" />
  </b-form>
</template>

<script>
import useVuelidate from "@vuelidate/core"
import bootstrapMixin from "@/mixins/bootstrapMixin"

export default {
  name: "b-text-input-form",
  mixins: [ bootstrapMixin ],
  props: {
    value: Object,
    consumer: {
      type: Function,
      required: true
    }
  },
  computed: {
    displayRequired() {
      return Object.values(this.inputs).map(input => {
        const inputRules = Object.keys(input.rules)
        const isRequired = inputRules.includes("required")
        const isLabelled = !!input.label
        return isLabelled && isRequired
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
      return Object.keys(this.inputs[key].rules).includes("required")
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
    async submit() {
      this.submitted = true
      await this.consumer()
      this.submitted = false
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