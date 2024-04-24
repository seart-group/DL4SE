<script>
import { BButton, BIconChevronDoubleUp } from "bootstrap-vue";

export default {
  name: "BackToTop",
  functional: true,
  props: {
    target: {
      type: String,
      required: true,
      validator(value) {
        return /#\S+/.test(value);
      },
    },
    offset: {
      type: Number,
      default: 0,
    },
  },
  render(createElement, { props, data }) {
    return createElement(
      "div",
      {
        props: props,
        attrs: data.attrs,
        class: {
          "back-to-top": true,
          ...(data.class || {}),
          ...Object.fromEntries(data.staticClass?.split(" ").map((sc) => [sc, true]) || []),
        },
        directives: data.directives ?? [],
        on: data.on,
      },
      [
        createElement(
          BButton,
          {
            class: "back-to-top-btn",
            directives: [
              {
                name: "scroll-to",
                rawName: "v-scroll-to",
                value: {
                  el: props.target,
                  offset: props.offset,
                },
              },
            ],
          },
          [createElement(BIconChevronDoubleUp)],
        ),
      ],
    );
  },
};
</script>
