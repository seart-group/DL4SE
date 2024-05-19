<template>
  <b-carousel :interval="currentInterval" :indicators="showIndicators" fade no-hover-pause no-touch>
    <b-carousel-slide v-for="([title, content], idx) in Object.entries(slides)" :key="idx">
      <template #img>
        <b-card no-body class="border-0 rounded-0">
          <b-card-body>
            <b-card-title class="text-center">{{ title }}</b-card-title>
            <b-card-text class="text-justify mb-4" v-html="content" />
          </b-card-body>
        </b-card>
      </template>
    </b-carousel-slide>
  </b-carousel>
</template>

<script>
export default {
  name: "b-text-carousel",
  props: {
    slides: {
      type: Object,
      default() {
        return {};
      },
    },
    interval: {
      type: Number,
      default: 10_000,
    },
  },
  computed: {
    currentInterval() {
      return this.visible ? this.interval : 0;
    },
    showIndicators() {
      return Object.keys(this.slides).length > 1;
    },
  },
  methods: {
    intersectionObserverCallback(entries) {
      entries.forEach((entry) => {
        this.visible = entry.isIntersecting;
      });
    },
  },
  mounted() {
    if (IntersectionObserver !== undefined) {
      this.observer = new IntersectionObserver(this.intersectionObserverCallback, {
        root: null,
        rootMargin: "-20px 0px",
        threshold: 0,
      });
      this.observer.observe(this.$el);
    }
  },
  beforeDestroy() {
    this.observer.disconnect();
  },
  data() {
    return {
      visible: undefined,
      observer: undefined,
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/text-carousel.sass" />
