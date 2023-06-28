<template>
  <div class="masking">
    <label :for="id + '-counter'" class="m-0"> Randomly mask&nbsp; </label>
    <p class="masking-pad" />
    <b-counter
      :id="id + '-counter'"
      placeholder="%"
      v-model.number="local.masking.percentage"
      :min="1"
      :max="100"
      :required="!!local.masking.token"
      class="py-2"
      counter-class="masking-counter-input"
    />
    <p class="m-0">&nbsp;of&nbsp;</p>
    <b-break />
    <p class="m-0">tokens&nbsp;</p>
    <label :for="id + '-token'" class="m-0"> using the&nbsp; </label>
    <div class="py-2">
      <b-input
        :id="id + '-token'"
        class="masking-token-input"
        placeholder="extra_id"
        v-model.trim="local.masking.token"
        @input="setToken"
        :state="tokenState"
      />
    </div>
    <p class="m-0">&nbsp;token</p>
    <b-break />
    <b-checkbox v-model="local.masking.contiguousOnly" :disabled="checkboxDisabled">
      Only mask contiguous tokens
    </b-checkbox>
  </div>
</template>

<script>
import useVuelidate from "@vuelidate/core"
import BBreak from "@/components/Break"
import BCounter from "@/components/Counter"
import { requiredIf } from "@vuelidate/validators"

export default {
  name: "b-masking",
  components: { BBreak, BCounter },
  props: {
    id: String,
    value: Object
  },
  computed: {
    percentageSpecified() {
      return this.local.masking.percentage != null
    },
    tokenSpecified() {
      return this.local.masking.token != null
    },
    anySpecified() {
      return !!this.percentageSpecified || !!this.tokenSpecified
    },
    anyEmpty() {
      return !this.local.masking.percentage || !this.local.masking.token
    },
    bothEmpty() {
      return !this.local.masking.percentage && !this.local.masking.token
    },
    tokenState() {
      return this.anySpecified ? !this.v$.local.masking.token.$invalid : null
    },
    checkboxDisabled() {
      return this.anyEmpty || this.v$.$invalid
    }
  },
  methods: {
    format(str) {
      const trimmed = str.trim()
      return trimmed ? trimmed : null
    },
    setToken(value) {
      this.local.masking.token = this.format(value)
    },
    resetCheckbox() {
      if (this.v$.$invalid) this.local.masking.contiguousOnly = false
      if (this.bothEmpty) this.local.masking.contiguousOnly = null
    },
    resetValidation() {
      if (this.bothEmpty) this.v$.$reset()
    }
  },
  watch: {
    "local.masking.percentage": {
      handler() {
        this.resetCheckbox()
        this.resetValidation()
        this.$emit("input", this.local.masking)
      }
    },
    "local.masking.token": {
      handler() {
        this.resetCheckbox()
        this.resetValidation()
        this.$emit("input", this.local.masking)
      }
    },
    "local.masking.contiguousOnly": {
      handler() {
        this.$emit("input", this.local.masking)
      }
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
      local: {
        masking: this.value
      }
    }
  },
  validations() {
    return {
      local: {
        masking: {
          token: {
            $autoDirty: true,
            required: requiredIf(this.percentageSpecified)
          }
        }
      }
    }
  }
}
</script>
