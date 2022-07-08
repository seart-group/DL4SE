<template>
  <b-dropdown :id="id" no-caret block
              toggle-class="dropdown-select-toggle"
              menu-class="dropdown-select-menu"
              :disabled="disabled"
  >
    <template #button-content>
      {{ toggleContent }}
    </template>
    <b-dropdown-header v-if="header">{{ header }}</b-dropdown-header>
    <b-dropdown-item v-for="option in options"
                     :key="option" :value="option"
                     @click="selected = option"
                     class="dropdown-select-item"
    >
      {{ option }}
    </b-dropdown-item>
  </b-dropdown>
</template>

<script>
import useVuelidate from "@vuelidate/core"
import {requiredIf} from "@vuelidate/validators"

export default {
  name: "b-dropdown-select",
  props: {
    id: String,
    value: [String, Number],
    required: Boolean,
    placeholder: {
      type: String,
      default: "Value"
    },
    header: {
      type: String,
      default: "Choose an option"
    },
    options: {
      type: Array,
      default() {
        return []
      }
    }
  },
  computed: {
    disabled() {
      return !this.options.length
    },
    toggleContent() {
      return (this.selected) ? this.selected : this.placeholder
    }
  },
  watch: {
    selected() {
      this.$emit('input', this.selected)
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
      selected: this.value
    }
  },
  validations() {
    return {
      selected: {
        $autoDirty: true,
        required: requiredIf(this.required)
      }
    }
  }
}
</script>