<template>
  <div class="counter">
    <b-input type="number" :id="id" :name="name" :placeholder="placeholder"
             v-model.number="count" :min="min" :max="max" @input="setCount" :state="validator()"
             class="counter-input"
    />
    <div class="counter-btn-group">
      <b-button type="button" @click="increment" class="counter-btn-top counter-btn-chevron-up" />
      <b-button type="button" @click="decrement" class="counter-btn-bottom counter-btn-chevron-down" />
    </div>
  </div>
</template>

<script>
export default {
  name: "b-counter",
  props: {
    id: String,
    name: String,
    value: {
      type: Number,
      default: null
    },
    min: {
      type: Number,
      default: Number.NEGATIVE_INFINITY,
      required: false
    },
    max: {
      type: Number,
      default: Number.POSITIVE_INFINITY,
      required: false
    },
    placeholder: String,
    validator: {
      type: Function,
      default() {
        return (this.value === null) ? null : (this.min <= this.value && this.value <= this.max)
      }
    }
  },
  methods: {
    toNumberOrNull(value) {
      let parsed = parseFloat(value)
      return (isNaN(parsed)) ? null : parsed
    },
    setCount(value) {
      this.count = this.toNumberOrNull(value)
    },
    increment() {
      if (this.count !== null && this.count < this.max) this.count += 1
      else this.count = 0
    },
    decrement() {
      if (this.count !== null && this.count > this.min) this.count -= 1
      else this.count = 0
    }
  },
  watch: {
    count() {
      this.$emit('input', this.toNumberOrNull(this.count))
    }
  },
  data() {
    return {
      count: this.value
    }
  }
}
</script>