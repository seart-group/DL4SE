<script>
export default {
  name: "b-abbreviation",
  functional: true,
  props: {
    value: {
      type: undefined,
      required: true,
    },
    formatter: {
      type: Function,
      default(value) {
        return value
      },
    },
  },
  render(createElement, { props, data }) {
    return createElement(
      "span",
      {
        attrs: data.attrs,
        class: data.staticClass,
        on: data.on,
        directives: [
          {
            name: "b-tooltip",
            rawName: "v-b-tooltip.html",
            value: props.value,
            modifiers: {
              html: true,
            },
          },
          ...(data.directives ?? []),
        ],
      },
      props.formatter(props.value),
    )
  },
}
</script>
