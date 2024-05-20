<template>
  <div class="d-flex position-relative align-items-center">
    <b-input
      type="number"
      :id="id"
      :placeholder="placeholder"
      v-model.number="count"
      :min="min"
      :max="max"
      :state="state"
      :disabled="disabled"
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
import useVuelidate from "@vuelidate/core";
import { between, requiredIf } from "@vuelidate/validators";

export default {
  name: "b-counter",
  props: {
    id: String,
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
    placeholder: String,
    required: Boolean,
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    state() {
      if (this.v$.$dirty || this.required) return !this.v$.$invalid;
      else return null;
    },
  },
  methods: {
    toNumberOrNull(value) {
      let parsed = parseFloat(value);
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
      if (!this.count) this.v$.$reset();
      this.$emit("input", this.toNumberOrNull(this.count));
    },
  },
  setup(props) {
    const globalConfig = props.id !== undefined ? { $registerAs: props.id } : {};
    return {
      v$: useVuelidate(globalConfig),
    };
  },
  data() {
    return {
      count: this.value,
    };
  },
  validations() {
    return {
      count: {
        $autoDirty: true,
        between: between(this.min, this.max),
        required: requiredIf(this.required),
      },
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/counter.sass" />
