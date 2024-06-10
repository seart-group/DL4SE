<template>
  <b-carousel :interval="currentInterval" :indicators="indicators" :fade="fade" no-hover-pause no-touch>
    <slot />
  </b-carousel>
</template>

<script>
export default {
  name: "b-text-carousel",
  props: {
    fade: {
      type: Boolean,
      default: true,
    },
    indicators: {
      type: Boolean,
      default: true,
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
