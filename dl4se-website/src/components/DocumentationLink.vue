<script>
import { BIconQuestionCircleFill, BLink } from "bootstrap-vue";

export default {
  name: "BDocumentationLink",
  functional: true,
  props: {
    page: {
      type: String,
      required: true,
    },
    section: {
      type: String,
      validator(value) {
        return value ? /#\S+/.test(value) : true;
      },
    },
  },
  render(createElement, { props, data }) {
    return createElement(
      BLink,
      {
        attrs: data.attrs,
        class: {
          "text-dark": true,
        },
        props: {
          to: {
            name: props.page,
            hash: props.section,
          },
        },
      },
      [createElement(BIconQuestionCircleFill)],
    );
  },
};
</script>
