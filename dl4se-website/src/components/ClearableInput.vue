<template>
  <b-input-group>
    <b-input type="text" :id="id" v-model.trim="input" :placeholder="placeholder" @input="setInput" />
    <b-input-group-append>
      <b-button @click="clear" type="secondary">
        <b-icon-x scale="1.5" />
      </b-button>
    </b-input-group-append>
  </b-input-group>
</template>

<script>
export default {
  name: "b-clearable-input",
  props: {
    id: String,
    value: {
      type: String,
      default: null,
    },
    placeholder: String,
  },
  methods: {
    setInput(value) {
      const trimmed = value?.trim();
      this.input = trimmed || null;
    },
    clear() {
      this.input = null;
    },
  },
  watch: {
    input() {
      this.$emit("input", this.input);
    },
  },
  data() {
    return {
      input: this.value,
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/clearable-input.sass" />
