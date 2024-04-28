<script>
import { BLink } from "bootstrap-vue";

export default {
  name: "b-logo",
  functional: true,
  components: { BLink },
  render(createElement, { data }) {
    return createElement(
      BLink,
      {
        props: {
          target: "_blank",
          href: "https://seart.si.usi.ch",
        },
        class: {
          ...(data.class || {}),
          ...Object.fromEntries(data.staticClass?.split(" ").map((sc) => [sc, true]) || []),
        },
      },
      [createElement("span", {}, "SE"), createElement("span", {}, "ART")],
    );
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/logo.sass" />
