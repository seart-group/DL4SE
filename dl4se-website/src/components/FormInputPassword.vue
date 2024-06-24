<template>
  <b-input-group>
    <b-input
      :id="id"
      :name="name"
      :type="type"
      :state="state"
      :disabled="disabled"
      :autocomplete="autocomplete"
      v-model.trim="input"
    />
    <b-input-group-append>
      <b-button :pressed.sync="pressed">
        <b-icon :icon="icon" />
      </b-button>
    </b-input-group-append>
  </b-input-group>
</template>

<script>
export default {
  name: "b-form-input-password",
  props: {
    id: {
      type: String,
      default: null,
    },
    value: String,
    name: {
      type: String,
      default: null,
    },
    state: {
      type: Boolean,
      default: null,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    autocomplete: {
      type: String,
      default: "off",
      validator(value) {
        const values = ["current-password", "new-password", "off"];
        return values.includes(value);
      },
    },
  },
  computed: {
    input: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit("input", value);
      },
    },
    icon() {
      return this.pressed ? "eye" : "eye-slash";
    },
    type() {
      return this.pressed ? "text" : "password";
    },
  },
  data() {
    return {
      pressed: false,
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/form-input-password.sass" />
