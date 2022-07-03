<!-- TODO: Make the template functional -->
<template>
  <b-iconstack v-bind="$attrs" v-on="$listeners">
    <b-icon-play-fill scale="0.5" shift-v="-1" stacked />
    <b-icon-calendar stacked />
  </b-iconstack>
</template>

<script>
export default {
  name: "b-icon-calendar-play"
}
</script>