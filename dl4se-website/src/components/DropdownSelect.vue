<template>
  <b-dropdown :id="id" no-caret block toggle-class="dropdown-toggle-btn"
              :disabled="!Array.isArray(options) || !options.length"
  >
    <template #button-content>
      {{ (selected) ? selected : placeholder }}
    </template>
    <b-dropdown-header v-if="header">{{ header }}</b-dropdown-header>
    <b-dropdown-item v-for="option in options"
                     :key="option" :value="option"
                     @click="selected = option"
    >
      {{ option }}
    </b-dropdown-item>
  </b-dropdown>
</template>

<script>
import useVuelidate from '@vuelidate/core'
import {requiredIf} from '@vuelidate/validators'

export default {
  name: "b-dropdown-select",
  props: {
    id: String,
    value: String,
    required: Boolean,
    placeholder: {
      type: String,
      default: "Value"
    },
    header: {
      type: String,
      default: "Choose an option"
    },
    options: Array[String]
  },
  watch: {
    selected() {
      this.$emit('input', this.selected)
    }
  },
  setup() {
    return {
      v$: useVuelidate()
    }
  },
  data() {
    return {
      selected: this.value
    }
  },
  validations() {
    return {
      selected: {
        required: requiredIf(this.required),
      }
    }
  }
}
</script>