<template>
  <b-dropdown :id="id" :disabled="disabled" no-caret block menu-class="w-100">
    <template #button-content>
      {{ selected ? selected : placeholder }}
    </template>
    <b-dropdown-header v-if="header">{{ header }}</b-dropdown-header>
    <b-dropdown-item v-for="option in options" :key="option" :value="option" @click="selected = option">
      {{ option ? option : placeholder }}
    </b-dropdown-item>
  </b-dropdown>
</template>

<script>
import useVuelidate from "@vuelidate/core";
import { requiredIf } from "@vuelidate/validators";

export default {
  name: "b-dropdown-select",
  props: {
    id: String,
    value: [String, Number],
    required: Boolean,
    disabled: Boolean,
    placeholder: {
      type: String,
      default: "None Selected",
    },
    header: {
      type: String,
      default: "Choose an option",
    },
    options: {
      type: Array,
      default: () => [],
    },
  },
  watch: {
    selected() {
      this.$emit("input", this.selected);
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
      selected: this.value,
    };
  },
  validations() {
    return {
      selected: {
        $autoDirty: true,
        required: requiredIf(this.required),
      },
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/dropdown-select.sass" />
