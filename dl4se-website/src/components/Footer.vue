<script>
import {BCol, BContainer, BLink, BRow} from "bootstrap-vue"

export default {
  name: "b-footer",
  functional: true,
  components: { BCol, BContainer, BLink, BRow },
  props: {
    authors: {
      type: Array,
      default() {
        return []
      },
    },
    organisation: {
      type: Object,
      default() {
        return {}
      },
    },
  },
  render(createElement, { props, data }) {
    const showAuthors = props.authors?.length
    const showOrganisation =
      props.organisation?.name !== undefined && props.organisation?.url !== undefined
    return createElement(
      BContainer,
      {
        props: {
          ...props,
          fluid: true,
        },
        attrs: data.attrs,
        class: {
          "footer-container": true,
          ...(data.class || {}),
          ...Object.fromEntries(data.staticClass?.split(" ").map((sc) => [sc, true]) || []),
        },
        directives: data.directives ?? [],
        on: data.on,
      },
      [
        createElement(
          BRow,
          {
            props: {
              alignH: "between",
            },
          },
          [
            ...(showAuthors
              ? [
                  createElement(
                    BCol,
                    {
                      props: {
                        md: "auto",
                        sm: "12",
                      },
                    },
                    [
                      createElement("div", {
                        class: "footer-text",
                        domProps: {
                          innerHTML: "Created by:&nbsp;",
                        },
                      }),
                      ...props.authors
                        .reduce((acc, author, idx) => {
                          return [
                            ...acc,
                            [
                              createElement(
                                BLink,
                                {
                                  key: `author-${idx}`,
                                  class: "footer-link",
                                  props: {
                                    target: "_blank",
                                    href: author.url,
                                  },
                                },
                                author.name,
                              ),
                              ...(idx + 1 < props.authors.length
                                ? [
                                    createElement("div", {
                                      key: `separator-${idx}`,
                                      class: "footer-link-separator",
                                      domProps: {
                                        innerHTML: ",&nbsp;",
                                      },
                                    }),
                                  ]
                                : []),
                            ],
                          ]
                        }, [])
                        .flatMap((nodes) => nodes),
                    ],
                  ),
                ]
              : []),
            ...(showOrganisation
              ? [
                  createElement(
                    BCol,
                    {
                      props: {
                        md: "auto",
                        sm: "12",
                      },
                    },
                    [
                      createElement("div", {
                        class: "footer-text",
                        domProps: {
                          innerHTML: "Maintained by:&nbsp;",
                        },
                      }),
                      createElement(
                        BLink,
                        {
                          class: "footer-link",
                          props: {
                            href: props.organisation.url,
                            target: "_blank",
                          },
                        },
                        props.organisation.name,
                      ),
                    ],
                  ),
                ]
              : []),
          ],
        ),
      ],
    )
  },
}
</script>
