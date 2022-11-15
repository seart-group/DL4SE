<template>
  <div class="back-to-top">
    <b-button v-scroll-to="target" class="back-to-top-btn">
      <b-icon-chevron-double-up />
    </b-button>
  </div>
</template>

<script>
export default {
  name: "BackToTop",
  props: {
    target: {
      type: String,
      required: true,
      validator(value) {
        return /#\S+/.test(value)
      }
    }
  }
}
</script>