import bootstrapMixin from "@/mixins/bootstrapMixin";

export default {
  mixins: [bootstrapMixin],
  methods: {
    redirectAndToast(location) {
      return (title, message, variant) => {
        this.$router.push(location).then(() => {
          this.appendToast(title, message, variant);
        });
      };
    },
    redirectHomeAndToast(title, message, variant) {
      this.redirectAndToast({ name: "home" })(title, message, variant);
    },
    redirectDashboardAndToast(title, message, variant) {
      this.redirectAndToast({ name: "dashboard" })(title, message, variant);
    },
  },
};
