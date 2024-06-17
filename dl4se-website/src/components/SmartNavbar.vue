<script>
import { BCollapse, BNavbar, BNavbarBrand, BNavbarNav, BNavbarToggle } from "bootstrap-vue";

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
    toggleable: {
      type: String,
      default: "sm",
    },
    sticky: {
      type: Boolean,
      default: false,
    },
  },
  render(createElement, { props, data }) {
    return createElement(
      BNavbar,
      {
        props: { ...props },
        attrs: { ...data.attrs },
        class: {
          ...(data.class || {}),
          ...Object.fromEntries(data.staticClass?.split(" ").map((sc) => [sc, true]) || []),
        },
        on: data.listeners,
        directives: data.directives ?? [],
      },
      [
        createElement(BNavbarBrand, {}, data.scopedSlots["brand"]()),
        createElement(BNavbarToggle, { props: { target: `${data.attrs.id}-collapse` } }, []),
        createElement(
          BCollapse,
          {
            attrs: { id: `${data.attrs.id}-collapse` },
            props: { isNav: true },
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
