<script>
import { BButton } from "bootstrap-vue";

export default {
  name: "b-form-submit",
  functional: true,
  components: { BButton },
  render(createElement, { props, data, slots }) {
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
      slots().default ?? ["Submit"],
    );
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/form-submit.sass" />
