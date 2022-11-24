<script>
import {BModal} from "bootstrap-vue"

export default {
  name: "b-dialog-modal",
  functional: true,
  props: {
    id: {
      type: String,
      required: true
    },
    title: {
      type: String,
      required: true
    }
  },
  render(createElement, { props, data, children }) {
    return createElement(
        BModal,
        {
          attrs: data.attrs,
          props: {
            id: props.id,
            title: props.title,
            contentClass: "dialog-modal-content",
            headerClass: "dialog-modal-header",
            bodyClass: "dialog-modal-body",
            footerClass: "dialog-modal-footer",
            scrollable: true,
            centered: true
          },
          directives: data.directives ?? [],
          on: data.on,
          scopedSlots: {
            "modal-footer": () => children
          }
        },
        []
    )
  }
}
</script>