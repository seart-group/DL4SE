<script>
import {BIconCalendar, BIconPlayFill, BIconstack} from "bootstrap-vue"

export default {
  name: "b-icon-calendar-play",
  functional: true,
  components: {
    BIconCalendar,
    BIconPlayFill,
    BIconstack
  },
  render(createElement, context) {
    return createElement(
        BIconstack,
        {
          props: context.props,
          attrs: context.data.attrs,
          class: context.data.staticClass,
          on: context.data.listeners,
          directives: context.data.directives
        },
        [
          createElement(BIconPlayFill, { props: { scale: 0.5, shiftV: -1, stacked: true } }),
          createElement(BIconCalendar, { props: { stacked: true } })
        ]
    )
  }
}
</script>