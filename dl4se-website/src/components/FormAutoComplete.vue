<!-- NOTE: Requires a wrapper with `relative` positioning -->
<template>
  <b-form-input
    :id="id"
    :name="name"
    :type="type"
    :state="state"
    :disabled="disabled"
    :placeholder="placeholder"
    autocomplete="off"
    v-model.trim="value"
  />
</template>

<script>
import Autocomplete from "bootstrap5-autocomplete/autocomplete.js";

export default {
  name: "b-form-auto-complete",
  props: {
    id: {
      type: String,
      default: null,
    },
    name: {
      type: String,
      default: null,
    },
    type: {
      type: String,
      default: "text",
      validator(value) {
        const types = ["text", "number", "email", "password", "search", "tel"];
        return types.includes(value);
      },
    },
    server: {
      type: String,
      required: true,
    },
    debounceTime: {
      type: Number,
      default: 100,
      validator(value) {
        return value >= 0;
      },
    },
    queryParam: {
      type: String,
      default: "q",
    },
    serverParams: {
      type: Object,
      default: () => ({}),
    },
    responseMapper: {
      type: Function,
      default: (json) => json,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    state: {
      type: Boolean,
      default: null,
    },
    placeholder: {
      type: String,
      default: "Type to show suggestions...",
    },
    notFoundMessage: {
      type: String,
      default: "No suggestions available...",
    },
    suggestionsThreshold: {
      type: Number,
      default: 0,
      validator(value) {
        return value >= 0;
      },
    },
    maximumItems: {
      type: Number,
      default: 5,
      validator(value) {
        return value > 0;
      },
    },
    autoSelectFirst: {
      type: Boolean,
      default: false,
    },
    highlightTyped: {
      type: Boolean,
      default: true,
    },
    fullWidth: {
      type: Boolean,
      default: true,
    },
  },
  watch: {
    value() {
      this.$emit("input", this.value);
    },
  },
  mounted() {
    new Autocomplete(this.$el, {
      server: this.server,
      debounceTime: this.debounceTime,
      queryParam: this.queryParam,
      serverParams: this.serverParams,
      maximumItems: this.maximumItems,
      suggestionsThreshold: this.suggestionsThreshold,
      notFoundMessage: this.notFoundMessage,
      onSelectItem: ({ label }) => (this.value = label),
      onServerResponse: (response) => response.json().then(this.responseMapper),
      highlightClass: "bg-transparent text-current text-decoration-underline p-0",
      activeClasses: ["bg-secondary", "text-white"],
      autoselectFirst: this.autoSelectFirst,
      highlightTyped: this.highlightTyped,
      fullWidth: this.fullWidth,
      liveServer: true,
      noCache: false,
    });
  },
  beforeDestroy() {
    Autocomplete.getInstance(this.$el).dispose();
  },
  data() {
    return {
      value: undefined,
    };
  },
};
</script>
