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
    dropdownShow: {
      type: Boolean,
      default: true
    }
  },
  render(createElement, context) {
    const dropdownItems = context.data.scopedSlots["dropdown-items"]()
    const navItems = context.data.scopedSlots["nav-items"]()
    return createElement(
        BNavbar,
        {
          props: {
            ...context.props,
            toggleable: "sm",
            sticky: true
          },
          attrs: context.data.attrs,
          class: `${context.data.staticClass} smart-navbar`,
          on: context.data.listeners,
          directives: context.data.directives
        },
        [
            createElement(BNavbarBrand, {}, context.props.brand),
            createElement(BNavbarToggle, { props: { target: `${context.props.id}-collapse` } }, []),
            createElement(
                BCollapse,
                {
                  props: {
                    id: `${context.props.id}-collapse`,
                    isNav: true
                  }
                },
                [
                    createElement(BNavbarNav, {}, navItems),
                    ... (context.props.dropdownShow)
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
                                        ...dropdownItems
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