<script>
import { BIconCalendar, BIconQuestion, BIconstack } from "bootstrap-vue";

export default {
  name: "b-icon-calendar-question",
  functional: true,
  components: {
    BIconCalendar,
    BIconQuestion,
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
        createElement(BIconQuestion, { props: { scale: 0.75, shiftV: -1, stacked: true } }),
        createElement(BIconCalendar, { props: { stacked: true } }),
      ],
    );
  },
};
</script>
