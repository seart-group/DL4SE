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
    contentClass: String,
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
              createElement(BCardTitle, { staticClass: props.titleClass }, data.scopedSlots["title"]()),
              createElement(BCardText, { staticClass: props.contentClass }, data.scopedSlots["content"]()),
            ]),
          ]),
        ],
      },
    });
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/text-carousel-slide.sass" />
