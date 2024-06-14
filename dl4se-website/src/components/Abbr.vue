<script>
export default {
  name: "b-abbr",
  functional: true,
  props: {
    title: {
      type: String,
      required: true,
    },
  },
  render(createElement, { props, data, children }) {
    return createElement(
      "abbr",
      {
        attrs: data.attrs,
        class: data.staticClass,
        on: data.on,
        directives: [
          {
            name: "b-tooltip",
            rawName: "v-b-tooltip.html",
            value: props.title,
            modifiers: {
              html: true,
            },
          },
          ...(data.directives ?? []),
        ],
      },
      children,
    );
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/abbr.sass" />
