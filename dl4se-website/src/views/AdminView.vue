<template>
  <div id="admin">
    <h1 class="d-none">Admin</h1>
    <b-container>
      <b-row>
        <b-col>
          <h2>Platform Users</h2>
          <b-paginated-table
            :id="userTable.id"
            :fields="userTable.fields"
            :controls="['filters']"
            :primary-key="userTable.fields[0].key"
            :total-items="userTable.totalItems"
            :provider="userProvider"
            :sticky-header="tableHeight"
          >
            <template #controls(filters)>
              <b-button v-b-modal.user-filter-select block class="btn-controls">
                <b-icon-filter class="align-middle" font-scale="1.5" />
                <span class="align-middle">Filter Settings</span>
              </b-button>
            </template>
            <template #cell(uid)="row">
              <b-icon-identicon :identifier="row.item.uid" :scale="1.35" /> {{ row.value }}
            </template>
            <template #cell(registered)="row">
              <b-abbreviation :value="row.value.toISOString()" :formatter="(iso) => iso.split('T')[0]" />
            </template>
            <template #cell(details)="row">
              <div class="d-inline-flex gap-2">
                <b-icon
                  :icon="`patch-${row.item.verified ? 'check' : 'question'}-fill`"
                  v-b-tooltip="`Email ${row.item.verified ? 'Verified' : 'Unverified'}`"
                  scale="1.35"
                />
                <b-iconstack v-b-tooltip="(row.item.enabled ? '' : 'Disabled ') + toTitle(row.item.role)" scale="1.35">
                  <b-icon
                    :icon="row.item.role === 'ADMIN' ? 'person-plus-fill' : 'person-fill'"
                    :shift-h="row.item.role === 'ADMIN' ? 2 : 0"
                    stacked
                  />
                  <b-icon icon="x-circle" stacked variant="danger" v-if="!row.item.enabled" />
                </b-iconstack>
              </div>
            </template>
            <template #cell(actions)="row">
              <div class="d-inline-flex gap-1">
                <b-button
                  @click="userAction(row.item.uid, row.item.enabled ? 'disable' : 'enable')"
                  v-b-tooltip="row.item.enabled ? 'Disable' : 'Enable'"
                  size="sm"
                >
                  <b-icon :icon="`person-${row.item.enabled ? 'x' : 'check'}-fill`" />
                </b-button>
                <b-button
                  @click="userAction(row.item.uid, row.item.role === 'ADMIN' ? 'demote' : 'promote')"
                  v-b-tooltip="row.item.role === 'ADMIN' ? 'Demote' : 'Promote'"
                  size="sm"
                >
                  <b-icon :icon="`person-${row.item.role === 'ADMIN' ? 'dash' : 'plus'}-fill`" />
                </b-button>
              </div>
            </template>
          </b-paginated-table>
        </b-col>
      </b-row>
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
    <b-dialog-modal
      id="user-filter-select"
      title="Specify User Filters"
      @hide="$root.$emit('bv::refresh::table', userTable.id)"
    >
      <label for="user-filter-uid">Filter by UID:</label>
      <b-clearable-input id="user-filter-uid" v-model="userTable.filters.uid" placeholder="User ID / Username" />
      <label for="user-filter-email">Filter by Email:</label>
      <b-clearable-input id="user-filter-email" v-model="userTable.filters.email" placeholder="example@email.com" />
      <label for="user-filter-organisation">Filter by Organisation:</label>
      <b-clearable-input
        id="user-filter-organisation"
        v-model="userTable.filters.organisation"
        placeholder="Organisation Name"
      />
    </b-dialog-modal>
  </div>
</template>

<script>
import bootstrapMixin from "@/mixins/bootstrapMixin";
import routerMixin from "@/mixins/routerMixin";
import BAbbreviation from "@/components/Abbreviation";
import BClearableInput from "@/components/ClearableInput";
import BConfigTable from "@/components/ConfigTable";
import BContentArea from "@/components/ContentArea";
import BDialogModal from "@/components/DialogModal";
import BIconIdenticon from "@/components/IconIdenticon";
import BPaginatedTable from "@/components/PaginatedTable";

export default {
  components: {
    BAbbreviation,
    BClearableInput,
    BConfigTable,
    BContentArea,
    BDialogModal,
    BIconIdenticon,
    BPaginatedTable,
  },
  mixins: [bootstrapMixin, routerMixin],
  computed: {
    tableHeight() {
      return `${this.$screen.xl ? 370 : 380}px`;
    },
  },
  methods: {
    toTitle(value) {
      return this.$_.startCase(this.$_.lowerCase(this.$_.defaultTo(value, "???")));
    },
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
    async userProvider(ctx) {
      const params = { page: ctx.currentPage - 1, size: ctx.perPage };
      if (ctx.sortBy) params.sort = `${ctx.sortBy},${ctx.sortDesc ? "desc" : "asc"}`;
      const filters = this.userTable.filters;
      if (filters.uid) params.uid = filters.uid;
      if (filters.email) params.email = filters.email;
      if (filters.organisation) params.organisation = filters.organisation;
      return this.$http
        .get("/admin/user", { params: params })
        .then((res) => {
          this.userTable.totalItems = res.data.total_items;
          return res.data.items;
        })
        .catch(() => {
          this.appendToast(
            "Error Fetching User Data",
            "There was a problem retrieving the user data. Refresh the page and try again.",
            "warning",
          );
        });
    },
    async userAction(uid, action) {
      const endpoint = `/admin/user/${uid}/${action}`;
      await this.$http.post(endpoint).catch((err) => {
        const status = err.response.status;
        switch (status) {
          case 401:
            this.$store.dispatch("logOut").then(() => {
              this.appendToast("Login Required", "Your session has expired. Please log in again.", "secondary");
            });
            break;
          case 403:
            this.$store.dispatch("logOut").then(() => {
              this.appendToast(
                "Access Restricted",
                "You do not have the necessary authorization to modify the requested resource.",
                "secondary",
              );
            });
            break;
          default:
            this.$router.push({ name: "home" });
            break;
        }
      });
      this.$root.$emit("bv::refresh::table", this.userTable.id);
    },
  },
  data() {
    return {
      disabled: false,
      userTable: {
        id: "user-table",
        filters: {
          uid: null,
          email: null,
          organisation: null,
        },
        fields: [
          {
            key: "uid",
            label: "UID",
            sortable: true,
            tdClass: ["text-monospace", "text-nowrap"],
          },
          {
            key: "email",
            sortable: true,
          },
          {
            key: "organisation",
            sortable: true,
          },
          {
            key: "registered",
            sortable: true,
            formatter: (value) => new Date(Date.parse(value + "Z")),
          },
          {
            key: "details",
            sortable: false,
          },
          {
            key: "actions",
            sortable: false,
          },
        ],
        totalItems: 0,
      },
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/view/admin.sass" />
