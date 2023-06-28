<template>
  <div class="counter">
    <b-input
      type="number"
      :id="id"
      :placeholder="placeholder"
      :class="counterClasses"
      v-model.number="count"
      :min="min"
      :max="max"
      :state="state"
      @input="setCount"
      @keydown.up.prevent="increment"
      @keydown.down.prevent="decrement"
    />
    <div class="counter-btn-group">
      <b-button
        type="button"
        @click="increment"
        class="counter-btn-top counter-btn-chevron-up"
        tabindex="-1"
      />
      <b-button
        type="button"
        @click="decrement"
        class="counter-btn-bottom counter-btn-chevron-down"
        tabindex="-1"
      />
    </div>
  </div>
</template>

<script>
import useVuelidate from "@vuelidate/core"
import { between, requiredIf } from "@vuelidate/validators"

export default {
  name: "b-counter",
  props: {
    id: String,
    counterClass: {
      type: String,
      default: ""
    },
    value: {
      type: Number,
      default: null
    },
    min: {
      type: Number,
      default: Number.MIN_SAFE_INTEGER
    },
    max: {
      type: Number,
      default: Number.MAX_SAFE_INTEGER
    },
    placeholder: String,
    required: Boolean
  },
  computed: {
    state() {
      if (this.v$.$dirty || this.required) return !this.v$.$invalid
      else return null
    },
    counterClasses() {
      const internal = ["counter-input"]
      return [...internal, ...this.counterClass.split(" ")]
    }
  },
  methods: {
    toNumberOrNull(value) {
      let parsed = parseFloat(value)
      return isNaN(parsed) ? null : parsed
    },
    setCount(value) {
      this.count = this.toNumberOrNull(value)
    },
    increment() {
      if (this.count !== null) {
        if (this.count < this.max) this.count += 1
      } else {
        this.count = this.min
      }
    },
    decrement() {
      if (this.count !== null) {
        if (this.count > this.min) this.count -= 1
      } else {
        this.count = this.min
      }
    }
  },
  watch: {
    count() {
      if (!this.count) this.v$.$reset()
      this.$emit("input", this.toNumberOrNull(this.count))
    }
  },
  setup(props) {
    const globalConfig = props.id !== undefined ? { $registerAs: props.id } : {}
    return {
      v$: useVuelidate(globalConfig)
    }
  },
  data() {
    return {
      count: this.value
    }
  },
  validations() {
    return {
      count: {
        $autoDirty: true,
        between: between(this.min, this.max),
        required: requiredIf(this.required)
      }
    }
  }
}
</script>
