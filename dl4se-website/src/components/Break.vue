<script>
export default {
  name: "b-break",
  functional: true,
  props: {
    xl: Boolean,
    lg: Boolean,
    md: Boolean,
    sm: Boolean,
    disabled: Boolean,
  },
  render(createElement, { props }) {
    const classes = {
      "w-100": true,
      "d-xl-none": props.xl,
      "d-lg-none": props.lg,
      "d-md-none": props.md,
      "d-sm-none": props.sm,
      "d-none": props.disabled,
    };
    return createElement("div", { class: classes });
  },
};
</script>
