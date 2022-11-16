<template>
  <b-modal :id="id" :title="title"
           content-class="dialog-modal-content"
           header-class="dialog-modal-header"
           body-class="dialog-modal-body"
           footer-class="dialog-modal-footer"
           scrollable centered
  >
    <template #modal-footer>
      <slot />
    </template>
  </b-modal>
</template>

<script>
export default {
  name: "b-dialog-modal",
  props: {
    id: {
      type: String,
      required: true
    },
    title: {
      type: String,
      required: true
    }
  }
}
</script>