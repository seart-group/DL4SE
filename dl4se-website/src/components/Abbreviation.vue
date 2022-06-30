<template>
  <span v-b-tooltip.html="value" v-html="abbreviated" />
</template>

<script>
export default {
  name: "b-abbreviation",
  props: {
    value: {
      type: undefined,
      required: true
    },
    transformer: {
      type: Function,
      default(value) {
        return value
      }
    }
  },
  computed: {
    abbreviated() {
      return this.transformer(this.value)
    }
  }
}
</script>