<template>
  <div class="range">
    <template v-if="lowerBound">
      <label :for="id + '-lower'" class="m-0">
        At least&nbsp;
      </label>
      <b-counter :id="id + '-lower'" class="py-2" placeholder="min"
                 :min="min" :max="lowerMax" v-model.number="count.lower"
      />
    </template>
    <template v-if="upperBound">
      <p class="m-0" v-if="lowerBound">&nbsp;and&nbsp;</p>
      <b-break md />
      <label :for="id + '-upper'" class="m-0">
        {{ (lowerBound) ? 'a' : 'A' }}t most&nbsp;
      </label>
      <b-counter :id="id + '-upper'" class="py-2" placeholder="max"
                 :min="upperMin" :max="max" v-model.number="count.upper"
      />
    </template>
    <p class="m-0" v-if="field">
      &nbsp;{{ field }}
    </p>
  </div>
</template>

<script>
import useVuelidate from "@vuelidate/core";
import BBreak from "@/components/Break"
import BCounter from "@/components/Counter";

export default {
  name: "b-range",
  components: { BBreak, BCounter },
  props: {
    id: String,
    field: String,
    lowerBound: Boolean,
    upperBound: Boolean,
    value: Object,
    min: {
      type: Number,
      default: Number.MIN_SAFE_INTEGER
    },
    max: {
      type: Number,
      default: Number.MAX_SAFE_INTEGER
    }
  },
  computed: {
    lowerMax() {
      return (this.count.upper !== null && this.count.upper <= this.max) ? this.count.upper : this.max
    },
    upperMin() {
      return (this.count.lower !== null && this.min <= this.count.lower) ? this.count.lower : this.min
    }
  },
  watch: {
    count: {
      deep: true,
      handler() {
        this.$emit("input", this.count)
      }
    }
  },
  setup(props) {
    const globalConfig = (props.id !== undefined) ? { $registerAs: props.id } : {}
    return {
      v$: useVuelidate(globalConfig)
    }
  },
  data() {
    return {
      count: this.value
    }
  }
}
</script>