<template>
  <div id="admin">
    <h1 class="d-none">Admin</h1>
    <b-container>
      <b-row>
        <b-col>
          <h2>Server Controls</h2>
          <b-content-area class="d-flex justify-content-md-start justify-content-around">
            <b-button @click="shutdownServer" variant="danger">
              <b-icon-power />
              Shutdown
            </b-button>
            <b-button @click="restartServer" variant="secondary">
              <b-icon-arrow-clockwise shift-h="-2" rotate="45" />
              Restart
            </b-button>
          </b-content-area>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import bootstrapMixin from "@/mixins/bootstrapMixin";
import routerMixin from "@/mixins/routerMixin";
import BContentArea from "@/components/ContentArea";

export default {
  components: { BContentArea },
  mixins: [bootstrapMixin, routerMixin],
  methods: {
    async shutdownServer() {
      this.showConfirmModal(
        "Shut Down Server",
        `You are about to shut down the server.
           Doing so will cause any currently executing tasks to be suspended,
           and the API unavailable until it is brought back up.
           Are you sure you want to continue?`,
      )
        .then((confirmed) => {
          return confirmed ? this.$http.post("/actuator/shutdown") : Promise.reject();
        })
        .then(() =>
          this.redirectHomeAndToast("Shutting Down Server", "The server has been successfully shut down.", "secondary"),
        )
        .catch(() => {
          // TODO: Differentiate between a user close and actual failure!
        });
    },
    async restartServer() {
      const restarted = await this.showConfirmModal(
        "Restart Server",
        `You are about to restart the server.
           Doing so will cause any currently executing tasks to be temporarily suspended.
           During this time the API will also be unavailable.
           Are you sure you want to continue?`,
      )
        .then((confirmed) => {
          return confirmed ? this.$http.post("/actuator/restart") : Promise.reject(false);
        })
        .then(() => {
          this.appendToast(
            "Restarting Server",
            "Server restart has been initiated. It may take a moment before it becomes available again.",
            "secondary",
          );
          return true;
        })
        .catch(() => false);

      if (!restarted) return;
      const that = this;
      const check = setInterval(async function () {
        await that.$http
          .get("/")
          .then(() => {
            clearInterval(check);
            that.appendToast("Server Connection Restored", "The DL4SE server is back online.", "secondary");
          })
          .catch(() => {});
      }, 5000);
    },
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/view/admin.sass" />
