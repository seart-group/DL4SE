<template>
  <div class="range">
    <template v-if="lowerBound">
      <label :for="id + '-lower'" class="m-0">
        At least&nbsp;
      </label>
      <b-counter :id="id + '-lower'" class="py-2"
                 :min="min" :max="max" placeholder="min"
                 v-model.number="count.lower"
                 :validator="lowerValid"
      />
    </template>
    <template v-if="upperBound">
      <p class="m-0" v-if="lowerBound">&nbsp;and&nbsp;</p>
      <b-break md />
      <label :for="id + '-upper'" class="m-0">
        {{ (lowerBound) ? 'a' : 'A' }}t most&nbsp;
      </label>
      <b-counter :id="id + '-upper'" class="py-2"
                 :min="min" :max="max" placeholder="max"
                 v-model.number="count.upper"
                 :validator="upperValid"
      />
    </template>
    <p class="m-0" v-if="field">
      &nbsp;{{ field }}
    </p>
  </div>
</template>

<script>
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
    state() {
      return this.isValid()
    }
  },
  methods: {
    lowerValid() {
      if (this.count.lower !== null) {
        if (this.count.upper !== null) {
          return this.min <= this.count.lower && this.count.lower <= this.count.upper
        } else {
          return this.min <= this.count.lower && this.count.lower <= this.max
        }
      } else {
        return null
      }
    },
    upperValid() {
      if (this.count.upper !== null) {
        if (this.count.lower !== null) {
          return this.count.lower <= this.count.upper && this.count.upper <= this.max
        } else {
          return this.min <= this.count.upper && this.count.upper <= this.max
        }
      } else {
        return null
      }
    },
    isValid() {
      return this.lowerValid() || this.upperValid()
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
  data() {
    return {
      count: this.value
    }
  }
}
</script>