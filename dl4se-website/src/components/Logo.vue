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
          logo: true,
          ...(data.class || {}),
        },
      },
      [
        createElement("span", { class: { "logo-box": true } }, "SE"),
        createElement("span", { class: { "logo-text": true } }, "ART"),
      ],
    );
  },
};
</script>
