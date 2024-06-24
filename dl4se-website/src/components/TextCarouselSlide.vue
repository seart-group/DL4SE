<script>
import { BCard, BCardBody, BCardFooter, BCardHeader, BCardText, BCardTitle, BCarouselSlide } from "bootstrap-vue";

export default {
  name: "b-text-carousel-slide",
  functional: true,
  components: {
    BCarouselSlide,
    BCard,
    BCardHeader,
    BCardBody,
    BCardFooter,
    BCardTitle,
    BCardText,
  },
  props: {
    titleClass: String,
    titleTag: {
      type: String,
      default: "h1",
      validator: function (value) {
        const headings = Array(6)
          .fill(1)
          .map((x, y) => x + y)
          .map((x) => `h${x}`);
        return headings.includes(value);
      },
    },
    bodyClass: String,
    bodyTag: String,
  },
  render(createElement, { props, data }) {
    return createElement(BCarouselSlide, {
      props: props,
      attrs: data.attrs,
      class: data.staticClass,
      directives: data.directives ?? [],
      on: data.on,
      scopedSlots: {
        img: () => [
          createElement(BCard, { props: { noBody: true } }, [
            createElement(BCardBody, {}, [
              createElement(
                BCardTitle,
                { props: { titleTag: props.titleTag }, staticClass: props.titleClass },
                data.scopedSlots["title"](),
              ),
              createElement(
                BCardText,
                { props: { bodyTag: props.bodyTag }, staticClass: props.bodyClass },
                data.scopedSlots["body"](),
              ),
            ]),
          ]),
        ],
      },
    });
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/text-carousel-slide.sass" />
