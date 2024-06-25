<template>
  <highlightjs :autodetect="autodetect" :language="language" :code="code" />
</template>

<script>
export default {
  name: "b-highlighted-code",
  props: {
    autodetect: {
      type: Boolean,
      default: false,
    },
    language: String,
    code: {
      type: String,
      default: "",
    },
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/highlighted-code.sass" />
