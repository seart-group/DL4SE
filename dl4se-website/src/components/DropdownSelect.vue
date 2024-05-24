<template>
  <b-dropdown :id="id" :disabled="disabled" no-caret block menu-class="w-100">
    <template #button-content>
      {{ selected ? selected : placeholder }}
    </template>
    <b-dropdown-header>
      <slot name="header">Choose an option</slot>
    </b-dropdown-header>
    <b-dropdown-item v-for="option in options" :key="option" :value="option" @click="selected = option">
      {{ option ? option : placeholder }}
    </b-dropdown-item>
  </b-dropdown>
</template>

<script>
export default {
  name: "b-dropdown-select",
  props: {
    id: {
      type: String,
      default: null,
    },
    value: [String, Number],
    disabled: {
      type: Boolean,
      default: false,
    },
    placeholder: {
      type: String,
      default: "None Selected",
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
  data() {
    return {
      selected: this.value,
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/dropdown-select.sass" />
