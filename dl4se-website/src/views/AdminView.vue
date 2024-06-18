<template>
  <div id="admin">
    <h1 class="d-none">Admin</h1>
    <b-container class="d-flex flex-column row-gap-3">
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
            <template #cell(organisation)="row">
              <span class="text-nowrap">{{ row.value }}</span>
            </template>
            <template #cell(registered)="row">
              <span v-b-tooltip="row.value.toISOString()">{{ row.value.toISOString().split("T")[0] }}</span>
            </template>
            <template #cell(details)="row">
              <div class="d-inline-flex gap-2">
                <b-icon
                  :icon="`patch-${row.item.verified ? 'check' : 'question'}-fill`"
                  v-b-tooltip="`Email ${row.item.verified ? 'Verified' : 'Unverified'}`"
                  scale="1.35"
                />
                <b-iconstack
                  v-b-tooltip="(row.item.enabled ? '' : 'Disabled ') + startCase(row.item.role)"
                  scale="1.35"
                >
                  <b-icon
                    :icon="row.item.role === 'ADMIN' ? 'person-plus-fill' : 'person-fill'"
                    :shift-h="row.item.role === 'ADMIN' ? 2 : 0"
                    stacked
                  />
                  <b-icon icon="x-lg" stacked variant="danger" v-if="!row.item.enabled" scale="0.8" />
                  <b-icon icon="circle" stacked variant="danger" v-if="!row.item.enabled" />
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
          <div class="table-config-container">
            <b-overlay :show="configTable.busy" variant="light">
              <b-table-simple
                :id="configTable.id"
                :hover="hasConfigs"
                :sticky-header="tableHeight"
                class="table-container"
                responsive
                borderless
              >
                <b-thead head-variant="dark">
                  <b-tr>
                    <b-th>Property</b-th>
                    <b-th>Value</b-th>
                  </b-tr>
                </b-thead>
                <b-tbody class="bg-light">
                  <template v-if="hasConfigs">
                    <b-tr v-for="key in Object.keys(configTable.configs)" :key="key">
                      <b-td>
                        <label :for="key" class="text-monospace m-0">
                          {{ key }}
                        </label>
                      </b-td>
                      <b-td>
                        <b-input
                          :id="key"
                          :state="configState(key)"
                          :disabled="configTable.busy"
                          v-model.trim="configTable.configs[key]"
                        />
                      </b-td>
                    </b-tr>
                  </template>
                  <template v-else>
                    <b-tr class="b-table-empty-row bg-light">
                      <b-td colspan="2">
                        <div role="alert" aria-live="polite">
                          <div class="text-center my-2">There are no records to show</div>
                        </div>
                      </b-td>
                    </b-tr>
                  </template>
                </b-tbody>
              </b-table-simple>
            </b-overlay>
            <b-container>
              <b-row align-h="center" no-gutters>
                <b-col cols="auto">
                  <b-button-group>
                    <b-button :disabled="disableSync" @click="configUpdate">
                      <b-icon-cloud-upload />
                      Synchronize
                    </b-button>
                    <b-button :disabled="configTable.busy" @click="configRefresh" class="ratio-1x1">
                      <b-icon-arrow-clockwise shift-h="-2" rotate="45" />
                    </b-button>
                  </b-button-group>
                </b-col>
              </b-row>
            </b-container>
          </div>
        </b-col>
      </b-row>
      <b-row>
        <b-col>
          <h2>Server Controls</h2>
          <b-content-area :class="{ 'gap-3': true, 'd-flex': $screen.sm, 'd-grid': !$screen.sm }">
            <b-button :disabled="disabled" @click="shutdownServer" variant="danger">
              <b-icon-power />
              Shutdown
            </b-button>
            <b-button :disabled="disabled" @click="restartServer">
              <b-icon-arrow-clockwise shift-h="-2" rotate="45" />
              Restart
            </b-button>
            <b-button :disabled="disabled" @click="getLog" variant="primary">
              <b-icon-file-earmark-text />
              Log
            </b-button>
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
import axios from "@/axios";
import { ref } from "vue";
import useVuelidate from "@vuelidate/core";
import { required } from "@vuelidate/validators";
import bootstrapMixin from "@/mixins/bootstrapMixin";
import formatterMixin from "@/mixins/formatterMixin";
import routerMixin from "@/mixins/routerMixin";
import BClearableInput from "@/components/ClearableInput";
import BContentArea from "@/components/ContentArea";
import BDialogModal from "@/components/DialogModal";
import BIconIdenticon from "@/components/IconIdenticon";
import BPaginatedTable from "@/components/PaginatedTable";

export default {
  components: {
    BClearableInput,
    BContentArea,
    BDialogModal,
    BIconIdenticon,
    BPaginatedTable,
  },
  mixins: [bootstrapMixin, formatterMixin, routerMixin],
  computed: {
    hasConfigs() {
      const configs = this.configTable.configs;
      return !!Object.keys(configs).length;
    },
    disableSync() {
      if (!this.hasConfigs) return true;
      const validator = this.v$.configTable.configs;
      return validator.$invalid || !validator.$anyDirty || this.configTable.busy;
    },
    tableHeight() {
      return `${this.$screen.xl ? 370 : 380}px`;
    },
  },
  methods: {
    configDirty(key) {
      return this.v$.configTable.configs[key].$dirty;
    },
    configValid(key) {
      return !this.v$.configTable.configs[key].$invalid;
    },
    configState(key) {
      return this.configDirty(key) ? this.configValid(key) : null;
    },
    async configUpdate() {
      const endpoint = "/admin/configuration";
      if (this.v$.configTable.$invalid) return;
      this.configTable.busy = true;
      this.$http
        .post(endpoint, this.configTable.configs)
        .then(() => {
          this.appendToast("Configuration Updated", "Server configuration has been successfully updated.", "secondary");
        })
        .catch(() => {
          this.appendToast(
            "Error Updating Configuration",
            "There was a problem updating the server configuration. Please try again.",
            "warning",
          );
        })
        .finally(() => {
          this.configTable.busy = false;
          this.v$.configTable.$reset();
        });
    },
    async configRefresh() {
      const endpoint = "/admin/configuration";
      this.configTable.busy = true;
      this.$http
        .get(endpoint)
        .then((res) => (this.configTable.configs = res.data))
        .catch(() => {
          this.appendToast(
            "Error Fetching Configuration",
            "There was a problem retrieving the server configuration. Please try again.",
            "warning",
          );
        })
        .finally(() => {
          this.configTable.busy = false;
          this.v$.configTable.$reset();
        });
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
            that.appendToast("Server Connection Restored", "The server is back online.", "secondary");
          })
          .finally(() => (that.disabled = false));
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
  setup() {
    const configTable = ref({
      id: "config-table",
      busy: false,
      configs: {},
    });
    (async () => {
      const endpoint = "/admin/configuration";
      configTable.value.configs = await axios.get(endpoint).then(({ data }) => data);
    })();
    return {
      v$: useVuelidate({ $autoDirty: true }),
      configTable: configTable,
    };
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
  validations() {
    const rules = { $autoDirty: true, required };
    return this.hasConfigs
      ? {
          configTable: {
            configs: Object.keys(this.configTable.configs).reduce(
              (acc, key) => Object.assign(acc, { [key]: rules }),
              {},
            ),
          },
        }
      : {};
  },
  head() {
    return {
      title: "Admin",
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/view/admin.sass" />
