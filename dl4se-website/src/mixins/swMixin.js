import bootstrapMixin from "@/mixins/bootstrapMixin";

export default {
  mixins: [bootstrapMixin],
  methods: {
    registrationFailed(event) {
      this.appendToast("Service worker registration failed", event.detail, "danger");
    },
    updateAvailable(event) {
      this.registration = event.detail;
      this.updateExists = true;
    },
    refreshApp() {
      this.updateExists = false;
      if (!this.registration?.waiting) return;
      this.registration.waiting.postMessage({ type: "SKIP_WAITING" });
    },
  },
  created() {
    document.addEventListener("swError", this.registrationFailed, { once: true });
    document.addEventListener("swUpdated", this.updateAvailable, { once: true });
    navigator.serviceWorker.addEventListener("controllerchange", () => {
      if (this.refreshing) return;
      this.refreshing = true;
      window.location.reload();
    });
  },
  data() {
    return {
      refreshing: false,
      registration: null,
      updateExists: false,
    };
  },
};
