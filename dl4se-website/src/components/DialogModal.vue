<script>
import { BModal } from "bootstrap-vue";

export default {
  name: "b-dialog-modal",
  functional: true,
  props: {
    id: {
      type: String,
      required: true,
    },
    title: {
      type: String,
      required: true,
    },
  },
  render(createElement, { props, data, children }) {
    return createElement(
      BModal,
      {
        attrs: data.attrs,
        props: {
          id: props.id,
          title: props.title,
          contentClass: "rounded-0",
          headerClass: "border-0",
          footerClass: "d-none",
          scrollable: Boolean(props.scrollable),
          centered: true,
        },
        directives: data.directives ?? [],
        on: data.on,
      },
      children,
    );
  },
};
</script>
