<template>
  <div id="profile">
    <h1 class="m-4">Welcome {{ uid }}!</h1>
  </div>
</template>

<script>
export default {
  props: {
    uid: String
  }
}
</script>
