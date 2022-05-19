<template>
  <div :id="'error-' + code" class="text-center">
    <h1 class="page-title">{{ code }}: {{ title }}</h1>
    <h3 class="m-5 px-5" v-if="description">
      {{ description }}
    </h3>
    <b-button to="/" class="btn btn-secondary border-dark border-2 text-center"><b-icon-house-door /> Home</b-button>
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
