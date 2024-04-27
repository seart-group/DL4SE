<script>
import {BCollapse, BNavbar, BNavbarBrand, BNavbarNav, BNavbarToggle} from "bootstrap-vue";

export default {
  name: "b-smart-navbar",
  functional: true,
  components: {
    BCollapse,
    BNavbar,
    BNavbarBrand,
    BNavbarNav,
    BNavbarToggle,
  },
  props: {
    id: {
      type: String,
      default: "smart-navbar",
    },
  },
  render(createElement, { props, data }) {
    return createElement(
      BNavbar,
      {
        props: {
          ...props,
          toggleable: "sm",
          sticky: true,
        },
        attrs: {
          id: props.id,
          ...data.attrs,
        },
        class: {
          ...(data.class || {}),
          ...Object.fromEntries(data.staticClass?.split(" ").map((sc) => [sc, true]) || []),
        },
        on: data.listeners,
        directives: data.directives ?? [],
      },
      [
        createElement(BNavbarBrand, {}, data.scopedSlots["brand"]()),
        createElement(BNavbarToggle, { props: { target: `${props.id}-collapse` } }, []),
        createElement(
          BCollapse,
          {
            props: {
              id: `${props.id}-collapse`,
              isNav: true,
            },
          },
          [
            createElement(BNavbarNav, {}, data.scopedSlots["nav-items-left"]()),
            createElement(BNavbarNav, { class: "ml-auto" }, data.scopedSlots["nav-items-right"]()),
          ],
        ),
      ],
    );
  },
};
</script>
