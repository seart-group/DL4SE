<script>
import {BJumbotron} from "bootstrap-vue"

export default {
  name: "b-content-area",
  functional: true,
  render(createElement, { props, data, children }) {
    return createElement(
        BJumbotron,
        {
          props: props,
          attrs: data.attrs,
          class: `${data.staticClass} content-area`,
          on: data.listeners
        },
        children
    )
  }
}
</script>