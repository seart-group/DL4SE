<template>
  <div id="admin">
    <h1 class="d-none">Admin</h1>
    <b-container>
      <b-row>
        <b-col>
          <h2>Server Environment</h2>
          <b-config-table :supplier="getConfiguration" :consumer="updateConfiguration" />
        </b-col>
      </b-row>
      <b-row>
        <b-col>
          <h2>Server Controls</h2>
          <b-content-area class="d-flex justify-content-around justify-content-md-start">
            <b-container>
              <b-row>
                <b-col cols="12" sm="auto">
                  <b-button @click="shutdownServer" variant="danger" class="mb-3 mb-sm-0" :disabled="disabled" block>
                    <b-icon-power />
                    Shutdown
                  </b-button>
                </b-col>
                <b-col cols="12" sm="auto">
                  <b-button @click="restartServer" variant="secondary" class="mb-3 mb-sm-0" :disabled="disabled" block>
                    <b-icon-arrow-clockwise shift-h="-2" rotate="45" />
                    Restart
                  </b-button>
                </b-col>
                <b-col cols="12" sm="auto">
                  <b-button @click="getLog" variant="primary" :disabled="disabled" block>
                    <b-icon-file-earmark-text />
                    Log
                  </b-button>
                </b-col>
              </b-row>
            </b-container>
          </b-content-area>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import bootstrapMixin from "@/mixins/bootstrapMixin";
import routerMixin from "@/mixins/routerMixin";
import BConfigTable from "@/components/ConfigTable";
import BContentArea from "@/components/ContentArea";

export default {
  components: {
    BConfigTable,
    BContentArea,
  },
  mixins: [bootstrapMixin, routerMixin],
  methods: {
    async getConfiguration() {
      const endpoint = "/admin/configuration";
      return this.$http.get(endpoint).then((res) => res.data);
    },
    async updateConfiguration(configuration) {
      const endpoint = "/admin/configuration";
      return this.$http.post(endpoint, configuration).then((res) => res.data);
    },
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
          this.disabled = true;
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
          .finally(() => {
            that.disabled = false;
          });
      }, 5000);
    },
    async getLog() {
      return this.$http
        .get("/actuator/logfile")
        .then((res) => res.data)
        .then((log) => {
          const options = { type: "text/plain" };
          const blob = new Blob([log], options);
          const url = URL.createObjectURL(blob);
          const a = document.createElement("a");
          a.href = url;
          a.download = "server.log";
          a.click();
          URL.revokeObjectURL(url);
        });
    },
  },
  data() {
    return {
      disabled: false,
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/view/admin.sass" />
