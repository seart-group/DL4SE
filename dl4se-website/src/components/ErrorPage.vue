<template>
  <div :id="'error-' + code" class="text-center">
    <h1 class="page-title">{{ code }}: {{ title }}</h1>
    <h3 class="m-5 px-5" v-if="description">
      {{ description }}
    </h3>
    <b-button to="/" class="action-btn"><b-icon-house-door /> Home</b-button>
  </div>
</template>

<script>
export default {
  name: "error-page",
  props: {
    code: Number,
    title: String,
    description: String
  }
}
</script>
