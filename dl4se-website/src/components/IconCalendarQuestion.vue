<!-- TODO: Make the template functional -->
<template>
  <b-iconstack v-bind="$attrs" v-on="$listeners">
    <b-icon-question scale="0.75" shift-v="-1" stacked />
    <b-icon-calendar stacked />
  </b-iconstack>
</template>

<script>
export default {
  name: "b-icon-calendar-question"
}
</script>