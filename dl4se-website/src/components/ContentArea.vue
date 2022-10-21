<script>
import {BJumbotron} from 'bootstrap-vue'

export default {
  name: "b-content-area",
  functional: true,
  render(createElement, context) {
    return createElement(
        BJumbotron,
        {
          props: context.props,
          attrs: context.data.attrs,
          class: `${context.data.staticClass} content-area`,
          on: context.data.listeners
        },
        context.children
    )
  }
}
</script>