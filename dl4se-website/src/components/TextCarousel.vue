<template>
  <b-carousel :interval="currentInterval" fade no-hover-pause no-touch class="text-carousel">
    <b-carousel-slide v-for="([title, content], idx) in Object.entries(slides)" :key="idx" class="text-carousel-slide">
      <b-card no-body class="text-carousel-card">
        <b-card-body>
          <b-card-title>{{ title }}</b-card-title>
          <b-card-text class="text-justify" v-html="content" />
        </b-card-body>
      </b-card>
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
        return {}
      }
    },
    interval: {
      type: Number,
      default: 10000
    }
  },
  computed: {
    currentInterval() {
      return this.visible ? this.interval : 0
    }
  },
  methods: {
    intersectionObserverCallback(entries){
      entries.forEach(entry => {
        this.visible = entry.isIntersecting;
      })
    }
  },
  mounted() {
    if (IntersectionObserver !== undefined) {
      const observer = new IntersectionObserver(
          this.intersectionObserverCallback,
          {
            root: null,
            rootMargin: '-20px 0px',
            threshold: 0
          }
      )
      observer.observe(this.$el)
    }
  },
  data() {
    return {
      visible: undefined
    }
  }
}
</script>