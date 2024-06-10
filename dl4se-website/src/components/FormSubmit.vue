<script>
import { BButton } from "bootstrap-vue";

export default {
  name: "b-form-submit",
  functional: true,
  components: { BButton },
  render(createElement, { props, data, children }) {
    return createElement(
      BButton,
      {
        props: {
          ...props,
          type: "submit",
        },
        attrs: data.attrs,
        class: data.staticClass,
        directives: data.directives ?? [],
        on: data.on,
      },
      children ?? ["Submit"],
    );
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/form-submit.sass" />
