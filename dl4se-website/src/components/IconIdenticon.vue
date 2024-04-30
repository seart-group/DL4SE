<script>
import Base64 from "crypto-js/enc-base64";
import md5 from "crypto-js/md5";
import Identicon from "identicon.js";

export default {
  name: "b-icon-identicon",
  functional: true,
  props: {
    identifier: {
      type: String,
      required: true,
    },
    scale: {
      type: Number,
      default: 1,
    },
    // TODO 22.11.22: Introduce support for other BIcon props
    // https://bootstrap-vue.org/docs/icons#component-reference
  },
  render(createElement, { props, data }) {
    const md = md5(props.identifier ?? "");
    const encoded = Base64.stringify(md);
    const options = {
      format: "svg",
      margin: 0,
      size: 16,
    };

    const document = new Identicon(encoded, options).render().getDump();
    const parser = new DOMParser();
    const element = parser.parseFromString(document, "text/html");
    const [svg] = element.body.children;
    const [g] = svg.children;
    g.removeAttribute("style");
    if (props.scale !== 1)
      g.setAttribute("transform", `translate(8 8) scale(${props.scale} ${props.scale}) translate(-8 -8)`);

    return createElement(
      "svg",
      {
        class: {
          "bi-identicon": true,
          "b-icon": true,
          bi: true,
          ...(data.class || {}),
          ...Object.fromEntries(data.staticClass?.split(" ").map((sc) => [sc, true]) || []),
        },
        attrs: {
          height: data.attrs.height || "1em",
          width: data.attrs.width || "1em",
          viewBox: "0 0 16 16",
          xmlns: "http://www.w3.org/2000/svg",
          focusable: false,
          role: "img",
          ariaLabel: "identicon",
          fill: "currentColor",
          stroke: "currentColor",
        },
        domProps: {
          innerHTML: g.outerHTML,
        },
        on: data.listeners,
        directives: data.directives,
      },
      [],
    );
  },
};
</script>
