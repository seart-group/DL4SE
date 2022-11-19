<script>
import {BIconCalendar, BIconExclamation, BIconstack} from "bootstrap-vue"

export default {
  name: "b-icon-calendar-exclamation",
  functional: true,
  components: {
    BIconCalendar,
    BIconExclamation,
    BIconstack
  },
  render(createElement, { props, data }) {
    return createElement(
        BIconstack,
        {
          props: props,
          attrs: data.attrs,
          class: data.staticClass,
          on: data.listeners,
          directives: data.directives
        },
        [
          createElement(BIconExclamation, { props: { scale: 0.75, shiftV: -1, stacked: true } }),
          createElement(BIconCalendar, { props: { stacked: true } })
        ]
    )
  }
}
</script>