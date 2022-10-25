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
          createElement(BIconQuestion, { props: { scale: 0.75, shiftV: -1, stacked: true } }),
          createElement(BIconCalendar, { props: { stacked: true } })
        ]
    )
  }
}
</script>