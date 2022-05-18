<template>
  <div class="input-group">
    <b-input type="number" :id="id" :name="name" :placeholder="placeholder"
             v-model.number="count" :min="min" :max="max" @input="setCount"
             class="form-control input-number bg-light-gray rounded-0 border-secondary border-left-0 border-top-0 border-right-0"
    />
    <span class="input-group-btn input-group-btn-vertical">
      <b-button type="button" @click="increment"
              class="btn bg-light-gray btn-outline-secondary rounded-0 border-0"
      >
        <b-icon-chevron-up :scale="scale" />
      </b-button>
      <b-button type="button" @click="decrement"
              class="btn bg-light-gray btn-outline-secondary rounded-0 border-secondary border-left-0 border-top-0 border-right-0 position-absolute"
      >
        <b-icon-chevron-down :scale="scale" />
      </b-button>
    </span>
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
    placeholder: String
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
      count: this.value,
      scale: 0.6
    }
  }
}
</script>

<style scoped>

input {
  min-height: 42px;
  max-width: 100px;
}

input:focus {
  box-shadow: 0 0 0 0.2rem rgb(108 117 125 / 50%);
}

input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  margin: 0;
  -webkit-appearance: none;
}

input[type=number] {
  -moz-appearance: textfield;
}

.btn-outline-secondary:focus, .btn-outline-secondary.focus {
  box-shadow: 0 0 0 0.2rem rgb(108 117 125 / 50%);
  z-index: 1;
}

.bg-light-gray {
  background-color: #e6e6e6;
}

.input-group-btn-vertical {
  position: relative;
  white-space: nowrap;
  vertical-align: middle;
  display: table-cell;
}

.input-group-btn-vertical > .btn {
  display: block;
  float: none;
  width: 100%;
  max-width: 100%;
  height: 50%;
  padding: 10px 10px;
  position: relative;
}

.input-group-btn-vertical > button > svg {
  position: absolute;
  top: 0;
  left: 0;
}
</style>