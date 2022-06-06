<template>
  <div class="masking">
    <label :for="id + '-counter'" class="m-0">
      Randomly mask&nbsp;
    </label>
    <p class="masking-pad" />
    <b-counter :id="id + '-counter'" class="py-2" counter-class="masking-counter-input"
               placeholder="%" :min="1" :max="100" v-model.number="local.masking.percentage"
               :validators="[ v$.local.masking.percentage ]"
    />
    <p class="m-0">
      &nbsp;of&nbsp;
    </p>
    <b-break md />
    <p class="m-0">
      tokens&nbsp;
    </p>
    <label :for="id + '-token'" class="m-0">
      using the&nbsp;
    </label>
    <div class="py-2">
      <b-input :id="id + '-token'" class="masking-token-input" placeholder="<MASK>"
               v-model="local.masking.token" @input="setToken" :state="inputState"
      />
    </div>
    <p class="m-0">
      &nbsp;token
    </p>
  </div>
</template>

<script>
import useVuelidate from "@vuelidate/core";
import BBreak from "@/components/Break"
import BCounter from "@/components/Counter";
import {requiredIf} from "@vuelidate/validators";

export default {
  name: "b-masking",
  components: { BBreak, BCounter },
  props: {
    id: String,
    value: Object
  },
  watch: {
    "local.masking": {
      deep: true,
      handler() {
        this.resetValidation()
        this.$emit("input", this.local.masking)
      }
    }
  },
  computed: {
    inputState() {
      return (this.v$.local.masking.$anyDirty) ? !this.v$.local.masking.token.$invalid : null
    }
  },
  methods: {
    format(str) {
      const trimmed = str.trim()
      return (trimmed) ? trimmed : null
    },
    setToken(value) {
      this.local.masking.token = this.format(value)
    },
    resetValidation() {
      if (this.local.masking.percentage === null && this.local.masking.token === null) this.v$.$reset()
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
      local: {
        masking: this.value
      }
    }
  },
  validations() {
    return {
      local: {
        masking: {
          percentage: {
            $autoDirty: true,
            required: requiredIf(this.local.masking.token)
          },
          token: {
            $autoDirty: true,
            required: requiredIf(this.local.masking.percentage)
          }
        }
      }
    }
  }
}
</script>