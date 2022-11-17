<script>
import {BButton, BIconChevronDoubleUp} from "bootstrap-vue"

export default {
  name: "BackToTop",
  functional: true,
  props: {
    target: {
      type: String,
      required: true,
      validator(value) {
        return /#\S+/.test(value)
      }
    }
  },
  render(createElement, { props, data }) {
    return createElement(
        "div",
        {
          props: props,
          attrs: data.attrs,
          class: ["back-to-top", data.staticClass].filter(Boolean).join(" "),
          on: data.listeners,
          directives: data.directives
        },
        [
            createElement(
                BButton,
                {
                  class: "back-to-top-btn",
                  directives: [
                    {
                      name: "scroll-to",
                      rawName: "v-scroll-to",
                      value: props.target
                    }
                  ]
                },
                [
                    createElement(BIconChevronDoubleUp)
                ]
            )
        ]
    )
  }
}
</script>