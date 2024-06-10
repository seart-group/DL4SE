<script>
import { BJumbotron } from "bootstrap-vue";

export default {
  name: "b-content-area",
  functional: true,
  render(createElement, { props, data, children }) {
    return createElement(
      BJumbotron,
      {
        props: {
          variant: "light",
          ...props,
        },
        attrs: data.attrs,
        class: {
          "content-area": true,
          ...(data.class || {}),
          ...Object.fromEntries(data.staticClass?.split(" ").map((sc) => [sc, true]) || []),
        },
        directives: data.directives ?? [],
        on: data.on,
      },
      children,
    );
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/content-area.sass" />
