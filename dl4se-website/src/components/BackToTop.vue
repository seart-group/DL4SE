<script>
import { BButton, BIconChevronDoubleUp } from "bootstrap-vue";

export default {
  name: "b-back-to-top",
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
            props: {
              variant: "light",
              squared: true,
            },
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

<style scoped lang="sass" src="@/assets/styles/component/back-to-top.sass" />
