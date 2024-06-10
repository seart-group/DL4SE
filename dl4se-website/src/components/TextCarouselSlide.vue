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
  render(createElement, { props, data }) {
    return createElement(BCarouselSlide, {
      props: props,
      attrs: data.attrs,
      class: data.staticClass,
      directives: data.directives ?? [],
      on: data.on,
      scopedSlots: {
        img: () => [
          createElement(BCard, { props: { "no-body": true } }, [
            createElement(BCardHeader),
            createElement(BCardBody, {}, [
              createElement(BCardTitle, {}, data.scopedSlots["title"]()),
              createElement(BCardText, {}, data.scopedSlots["content"]()),
            ]),
            createElement(BCardFooter),
          ]),
        ],
      },
    });
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/text-carousel-slide.sass" />
