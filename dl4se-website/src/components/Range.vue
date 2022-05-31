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
      <label :for="id + '-upper'" class="m-0">
        {{ ((lowerBound) ? 'a' : 'A') + "t most&nbsp;" }}
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
import BCounter from "@/components/Counter";

export default {
  name: "b-range",
  components: { BCounter },
  props: {
    id: String,
    field: {
      type: String,
      required: false
    },
    lowerBound: {
      type: Boolean,
    },
    upperBound: {
      type: Boolean,
    },
    lower: {
      type: Number,
      default: null
    },
    upper: {
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
    }
  },
  computed: {
    state() {
      const inputs = [this.lowerValid(), this.upperValid()]
      return inputs.filter(x => x !== null).reduce((acc, curr) => acc && curr, true)
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
    }
  },
  watch: {
    "count.lower": function () {
      this.$emit("update:lower", this.count.lower)
    },
    "count.upper": function() {
      this.$emit("update:upper", this.count.upper)
    }
  },
  data() {
    return {
      count: {
        lower: this.lower,
        upper: this.upper
      }
    }
  }
}
</script>