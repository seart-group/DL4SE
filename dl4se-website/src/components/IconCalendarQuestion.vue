<script>
import {BIconCalendar, BIconQuestion, BIconstack} from "bootstrap-vue"

export default {
  name: "b-icon-calendar-question",
  functional: true,
  components: {
    BIconCalendar,
    BIconQuestion,
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
          createElement(BIconQuestion, { props: { scale: 0.75, shiftV: -1, stacked: true } }),
          createElement(BIconCalendar, { props: { stacked: true } })
        ]
    )
  }
}
</script>