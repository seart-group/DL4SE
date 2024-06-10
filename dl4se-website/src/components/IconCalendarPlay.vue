<script>
import { BIconCalendar, BIconPlayFill, BIconstack } from "bootstrap-vue";

export default {
  name: "b-icon-calendar-play",
  functional: true,
  components: {
    BIconCalendar,
    BIconPlayFill,
    BIconstack,
  },
  render(createElement, { props, data }) {
    return createElement(
      BIconstack,
      {
        props: props,
        attrs: data.attrs,
        class: data.staticClass,
        directives: data.directives ?? [],
        on: data.on,
      },
      [
        createElement(BIconPlayFill, { props: { scale: 0.5, shiftV: -1, stacked: true } }),
        createElement(BIconCalendar, { props: { stacked: true } }),
      ],
    );
  },
};
</script>
