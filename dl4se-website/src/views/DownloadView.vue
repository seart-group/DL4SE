<template>
  <div id="download" v-if="show">
    <h1 class="d-none">Download</h1>
    <b-container>
      <b-row>
        <b-col>
          <h2 class="text-center">Download will commence shortly</h2>
          <p class="text-center">You will be redirected back to the dashboard in {{ timer }}...</p>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import routerMixin from "@/mixins/routerMixin";

export default {
  props: {
    uuid: String,
  },
  mixins: [routerMixin],
  async created() {
    await this.$http
      .get(`/task/${this.uuid}/token`)
      .then(({ data: token }) => {
        window.location.href = `${process.env.VUE_APP_API_BASE_URL}/task/${this.uuid}/download?token=${token}`;
        this.show = true;
      })
      .catch((err) => {
        const status = err.response.status;
        switch (status) {
          case 400:
            this.redirectDashboardAndToast(
              "Invalid UUID",
              "The specified task UUID is not valid. Make sure you copied the link correctly, and try again.",
              "warning",
            );
            break;
          case 401:
            this.$store.dispatch("logOut").then(() => {
              this.appendToast("Login Required", "Your session has expired. Please log in again.", "secondary");
            });
            break;
          case 403:
            this.redirectDashboardAndToast("Task Download Refused", "This task can not be downloaded.", "warning");
            break;
          case 404:
            this.redirectDashboardAndToast("Task Not Found", "The specified task could not be found.", "warning");
            break;
          case 410:
            this.redirectDashboardAndToast(
              "Task Download Expired",
              "The download link for this task is no longer valid.",
              "secondary",
            );
            break;
          default:
            this.$router.push({ name: "home" });
        }
      });
  },
  watch: {
    timer: {
      handler(value) {
        if (value > 0) {
          setTimeout(() => {
            this.timer -= 1;
          }, 1000);
        } else {
          this.$router.push({ name: "dashboard" });
        }
      },
      immediate: true,
    },
  },
  data() {
    return {
      timer: 5,
      show: false,
    };
  },
};
</script>
