<template>
  <div class="counter">
    <b-input type="number" :id="id" :name="name" :placeholder="placeholder" :class="counterClasses"
             v-model.number="count" :min="min" :max="max" :state="validator()"
             @input="setCount" @keydown.up.prevent="increment" @keydown.down.prevent="decrement"
    />
    <div class="counter-btn-group">
      <b-button type="button" @click="increment" class="counter-btn-top counter-btn-chevron-up" tabindex="-1" />
      <b-button type="button" @click="decrement" class="counter-btn-bottom counter-btn-chevron-down" tabindex="-1" />
    </div>
  </div>
</template>

<script>
export default {
  name: "b-counter",
  props: {
    id: String,
    name: String,
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
      default: Number.MIN_SAFE_INTEGER,
      required: false
    },
    max: {
      type: Number,
      default: Number.MAX_SAFE_INTEGER,
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
  computed: {
    counterClasses() {
      const internal = [ "counter-input" ]
      return [...internal, ...this.counterClass.split(" ")]
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