<template>
  <div class="d-flex position-relative align-items-center">
    <b-input
      type="number"
      :id="id"
      :min="min"
      :max="max"
      :state="state"
      :disabled="disabled"
      :required="required"
      :placeholder="placeholder"
      v-model.number="count"
      @input="setCount"
      @keydown.up.prevent="increment"
      @keydown.down.prevent="decrement"
    />
    <div class="d-table-cell position-relative align-middle text-nowrap">
      <b-button :disabled="disabled" @click="increment" tabindex="-1" class="btn-up" />
      <b-button :disabled="disabled" @click="decrement" tabindex="-1" class="btn-down" />
    </div>
  </div>
</template>

<script>
export default {
  name: "b-counter",
  props: {
    id: {
      type: String,
      default: null,
    },
    value: {
      type: Number,
      default: null,
    },
    min: {
      type: Number,
      default: Number.MIN_SAFE_INTEGER,
    },
    max: {
      type: Number,
      default: Number.MAX_SAFE_INTEGER,
    },
    placeholder: {
      type: String,
      default: null,
    },
    required: {
      type: Boolean,
      default: false,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    state: {
      type: Boolean,
      default: null,
    },
  },
  methods: {
    toNumberOrNull(value) {
      const parsed = parseFloat(value);
      return isNaN(parsed) ? null : parsed;
    },
    setCount(value) {
      this.count = this.toNumberOrNull(value);
    },
    increment() {
      if (this.count !== null) {
        if (this.count < this.max) this.count += 1;
      } else {
        this.count = this.min;
      }
    },
    decrement() {
      if (this.count !== null) {
        if (this.count > this.min) this.count -= 1;
      } else {
        this.count = this.min;
      }
    },
  },
  watch: {
    count() {
      this.$emit("input", this.toNumberOrNull(this.count));
    },
  },
  data() {
    return {
      count: this.value,
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/counter.sass" />
