<script>
import {
  BCollapse,
  BIconPersonFill,
  BNavbar,
  BNavbarBrand,
  BNavbarNav,
  BNavbarToggle,
  BNavItemDropdown
} from "bootstrap-vue"

export default {
  name: "b-smart-navbar",
  functional: true,
  components: {
    BCollapse,
    BIconPersonFill,
    BNavbar,
    BNavbarBrand,
    BNavItemDropdown,
    BNavbarNav,
    BNavbarToggle
  },
  props: {
    id: {
      type: [String, Number],
      default: "smart-navbar"
    },
    brand: {
      type: String,
      default: "App"
    },
    showDropdown: {
      type: Boolean,
      default: true
    }
  },
  render(createElement, { props, data }) {
    return createElement(
        BNavbar,
        {
          props: {
            ...props,
            toggleable: "sm",
            sticky: true
          },
          attrs: data.attrs,
          class: `${data.staticClass} smart-navbar`,
          on: data.listeners,
          directives: data.directives
        },
        [
            createElement(BNavbarBrand, {}, props.brand),
            createElement(BNavbarToggle, { props: { target: `${props.id}-collapse` } }, []),
            createElement(
                BCollapse,
                {
                  props: {
                    id: `${props.id}-collapse`,
                    isNav: true
                  }
                },
                [
                    createElement(BNavbarNav, {}, data.scopedSlots["nav-items"]()),
                    ... (props.showDropdown)
                        ? [
                            createElement(
                                BNavbarNav,
                                {
                                  class: "ml-auto"
                                },
                                [
                                  createElement(
                                      BNavItemDropdown,
                                      {
                                        props: {
                                          right: true
                                        }
                                      },
                                      [
                                        createElement(
                                            "template",
                                            {
                                              slot: "button-content"
                                            },
                                            [
                                                createElement(BIconPersonFill)
                                            ]
                                        ),
                                        ...data.scopedSlots["dropdown-items"]()
                                      ]
                                  )
                                ]
                            )
                        ]
                        : []
                ]
            )
        ]
    )
  }
}
</script>