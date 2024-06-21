<script>
export default {
  name: "b-preformatted-code",
  functional: true,
  render(createElement, { data, children }) {
    return createElement(
      "pre",
      {
        class: data.staticClass,
        style: data.staticStyle,
      },
      [createElement("code", { class: "text-monospace" }, children)],
    );
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/preformatted-code.sass" />
