export default {
  data() {
    return {
      scroll: {
        x: 0,
        y: 0,
      },
    };
  },
  created() {
    if (!this.$isServer) {
      this._scrollListener = () => {
        this.scroll = {
          x: Math.round(window.scrollX),
          y: Math.round(window.scrollY),
        };
      };

      this._scrollListener();
      window.addEventListener("scroll", this._scrollListener);
    }
  },
  beforeDestroy() {
    window.removeEventListener("scroll", this._scrollListener);
  },
};
