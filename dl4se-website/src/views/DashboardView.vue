<template>
  <div id="dashboard">
    <h1 class="d-none">Dashboard</h1>
    <b-container>
      <b-row>
        <b-col>
          <h2>Requested Datasets</h2>
        </b-col>
      </b-row>
      <b-row align-h="center">
        <b-col>
          <b-paginated-table
            :id="taskTable.id"
            :fields="taskTable.fields"
            :controls="['code', 'filters']"
            :primary-key="taskTable.fields[0].key"
            :total-items="taskTable.totalItems"
            :provider="taskProvider"
            :sticky-header="tableHeight"
          >
            <template #controls(code)>
              <b-button :to="{ name: 'code' }" block class="btn-controls">
                <b-icon-plus class="align-middle" font-scale="1.5" />
                <span class="align-middle">Create New Dataset</span>
              </b-button>
            </template>
            <template #controls(filters)>
              <b-button v-b-modal.task-filter-select block class="btn-controls">
                <b-icon-filter class="align-middle" font-scale="1.5" />
                <span class="align-middle">Filter Settings</span>
              </b-button>
            </template>
            <template #cell(uuid)="row">
              <b-abbr
                v-if="!$screen.md"
                :title="`<span class='text-monospace'>${row.value}</span>`"
                class="text-monospace"
              >
                {{ row.value.split("-")[0] }}
              </b-abbr>
              <span v-else class="text-monospace">{{ row.value }}</span>
            </template>
            <template #cell(status)="row">
              <b-icon :icon="statusToSquareIcon(row.value)" v-b-tooltip="startCase(row.value)" font-scale="1.25" />
            </template>
            <template #cell(submitted)="row">
              <div class="d-inline-flex align-items-center">
                <template v-if="row.value.submitted">
                  <b-icon-calendar-plus
                    v-b-tooltip.html="`Submitted at:<br />${row.value.submitted.toISOString()}`"
                    font-scale="1.35"
                  />
                </template>
                <template v-if="row.value.started">
                  <b-icon-dash-lg shift-v="-1" />
                  <b-icon-calendar-play
                    v-b-tooltip.html="`Started at:<br />${row.value.started.toISOString()}`"
                    font-scale="1.35"
                  />
                </template>
                <template v-else>
                  <b-icon-blank />
                  <b-icon-blank font-scale="1.35" />
                </template>
                <template v-if="row.value.finished">
                  <b-icon-dash-lg shift-v="-1" />
                  <component
                    :is="statusToCalendarIcon(row.item.status)"
                    v-b-tooltip.html="`${startCase(row.item.status)} at:<br />${row.value.finished.toISOString()}`"
                    font-scale="1.35"
                  />
                </template>
                <template v-else>
                  <b-icon-blank />
                  <b-icon-blank font-scale="1.35" />
                </template>
              </div>
            </template>
            <template #cell(progress)="row">
              <div class="d-flex flex-column text-center">
                <template v-if="row.value.status === 'FINISHED' && !row.value.total">
                  <span class="text-nowrap">No Results</span>
                </template>
                <template v-else>
                  <span>{{ row.value.percentage }}</span>
                  <b-progress
                    :max="row.value.total"
                    :value="row.value.processed"
                    v-b-tooltip.html="`Total Instances:<br />${row.value.total}`"
                    tabindex="0"
                  />
                </template>
              </div>
            </template>
            <template #cell(details)="row">
              <div class="d-inline-flex gap-1">
                <b-button
                  @click="display('Submitter', row.item.user, $event.target)"
                  v-b-tooltip="'Show User Details'"
                  size="sm"
                >
                  <b-icon-person-lines-fill />
                </b-button>
                <b-button
                  @click="display('Query', row.item.query, $event.target)"
                  v-b-tooltip="'Show Query Details'"
                  size="sm"
                >
                  <b-icon-search />
                </b-button>
                <b-button
                  @click="display('Processing', row.item.processing, $event.target)"
                  v-b-tooltip="'Show Processing Details'"
                  size="sm"
                >
                  <b-icon-gear-fill />
                </b-button>
              </div>
            </template>
            <template #cell(actions)="row">
              <div class="d-inline-flex gap-1">
                <template v-if="['FINISHED', 'CANCELLED', 'ERROR'].includes(row.item.status)">
                  <span class="d-inline-block" tabindex="0" v-b-tooltip="'Cancel Task'">
                    <b-button size="sm" disabled>
                      <b-icon-trash />
                    </b-button>
                  </span>
                </template>
                <template v-else>
                  <b-button @click="taskCancel(row.item.uuid)" v-b-tooltip="'Cancel Task'" size="sm">
                    <b-icon-trash />
                  </b-button>
                </template>
                <b-button v-b-tooltip="'Edit Task'" :to="{ name: 'code', params: { uuid: row.item.uuid } }" size="sm">
                  <b-icon-pencil-square />
                </b-button>
                <template v-if="row.item.status !== 'FINISHED' || row.item.expired || row.item.total_results === 0">
                  <span class="d-inline-block" tabindex="0" v-b-tooltip="'Download Results'">
                    <b-button size="sm" disabled>
                      <b-icon-download />
                    </b-button>
                  </span>
                </template>
                <template v-else>
                  <b-button
                    :to="{ name: 'download', params: { uuid: row.item.uuid } }"
                    v-b-tooltip="'Download Results'"
                    size="sm"
                  >
                    <b-icon-download />
                  </b-button>
                </template>
              </div>
            </template>
          </b-paginated-table>
        </b-col>
      </b-row>
    </b-container>
    <b-details-modal
      :id="detailsModal.id"
      :title="detailsModal.title"
      :content="detailsModal.content"
      :formatters="detailsModal.formatters"
      :tabs-align="!$screen.md ? 'center' : 'left'"
      :footer-button-block="!$screen.md"
      @reset="
        detailsModal.title = '';
        detailsModal.content = {};
      "
    />
    <b-dialog-modal
      id="task-filter-select"
      title="Specify Task Filters"
      @hide="$root.$emit('bv::refresh::table', taskTable.id)"
    >
      <label for="task-filter-uuid">Filter by UUID:</label>
      <b-clearable-input id="task-filter-uuid" v-model="taskTable.filters.uuid" placeholder="Partial / Full UUID" />
      <p class="d-inline-block mb-2 cursor-default" @click="$refs.taskFilterStatus.$el.querySelector('button').focus()">
        Filter by Status:
      </p>
      <b-dropdown-select
        id="task-filter-status"
        ref="taskFilterStatus"
        aria-label="Filter by Status:"
        v-model="taskTable.filters.status"
        :options="[null, 'QUEUED', 'EXECUTING', 'FINISHED', 'CANCELLED', 'ERROR']"
        placeholder="ALL"
      />
    </b-dialog-modal>
  </div>
</template>

<script>
import bootstrapMixin from "@/mixins/bootstrapMixin";
import formatterMixin from "@/mixins/formatterMixin";
import routerMixin from "@/mixins/routerMixin";
import BAbbr from "@/components/Abbr";
import BClearableInput from "@/components/ClearableInput";
import BDetailsModal from "@/components/DetailsModal";
import BDialogModal from "@/components/DialogModal";
import BDropdownSelect from "@/components/DropdownSelect";
import BIconCalendarExclamation from "@/components/IconCalendarExclamation";
import BIconCalendarPlay from "@/components/IconCalendarPlay";
import BIconCalendarQuestion from "@/components/IconCalendarQuestion";
import BPaginatedTable from "@/components/PaginatedTable";
import BContentArea from "@/components/ContentArea.vue";

export default {
  components: {
    BContentArea,
    BAbbr,
    BClearableInput,
    BDetailsModal,
    BDialogModal,
    BDropdownSelect,
    BIconCalendarExclamation,
    BIconCalendarPlay,
    BIconCalendarQuestion,
    BPaginatedTable,
  },
  mixins: [bootstrapMixin, formatterMixin, routerMixin],
  computed: {
    tableHeight() {
      return `${this.$screen.xl ? 370 : 380}px`;
    },
  },
  methods: {
    statusToSquareIcon(status) {
      switch (status) {
        case "QUEUED":
          return "plus-square-fill";
        case "EXECUTING":
          return "caret-right-square-fill";
        case "FINISHED":
          return "check-square-fill";
        case "CANCELLED":
          return "x-square-fill";
        case "ERROR":
          return "exclamation-square-fill";
        default:
          return "question-square-fill";
      }
    },
    statusToCalendarIcon(status) {
      switch (status) {
        case "QUEUED":
          return "b-icon-calendar-plus";
        case "EXECUTING":
          return "b-icon-calendar-play";
        case "FINISHED":
          return "b-icon-calendar-check";
        case "CANCELLED":
          return "b-icon-calendar-x";
        case "ERROR":
          return "b-icon-calendar-exclamation";
        default:
          return "b-icon-calendar-question";
      }
    },
    async taskProvider(ctx) {
      const params = { page: ctx.currentPage - 1, size: ctx.perPage };
      if (ctx.sortBy) params.sort = `${ctx.sortBy},${ctx.sortDesc ? "desc" : "asc"}`;
      const filters = this.taskTable.filters;
      if (filters.uuid) params.uuid = filters.uuid;
      if (filters.status) params.status = filters.status;
      return this.$http
        .get("/task", { params: params })
        .then((res) => {
          this.taskTable.totalItems = res.data.total_items;
          return res.data.items;
        })
        .catch(() => {
          this.appendToast(
            "Error Fetching Task Data",
            "There was a problem retrieving the task data. Refresh the page and try again.",
            "warning",
          );
        });
    },
    async taskCancel(uuid) {
      const endpoint = `/task/${uuid}/cancel`;
      await this.$http.post(endpoint).catch((err) => {
        const status = err.response.status;
        switch (status) {
          case 400:
            this.appendToast(
              "Cannot cancel task",
              "The task has already finished executing and can not be cancelled.",
              "secondary",
            );
            break;
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
      this.$root.$emit("bv::refresh::table", this.taskTable.id);
    },
    display(title, item, button) {
      this.detailsModal.title = title;
      this.detailsModal.content = item;
      this.$root.$emit("bv::show::modal", this.detailsModal.id, button);
    },
  },
  data() {
    return {
      detailsModal: {
        id: "details-modal",
        title: "",
        content: "",
        formatters: [
          {
            name: "JSON",
            formatter: this.formatObjectAsJson,
          },
          {
            name: "Text",
            formatter: this.formatObjectAsTextList,
          },
        ],
      },
      taskTable: {
        id: "task-table",
        filters: {
          uuid: null,
          status: null,
        },
        fields: [
          {
            key: "uuid",
            label: "UUID",
            sortable: true,
            tdClass: ["text-nowrap", "text-monospace"],
          },
          {
            key: "status",
            sortable: true,
            tdClass: ["text-center"],
          },
          {
            key: "submitted",
            label: "Timeline",
            sortable: true,
            formatter: (_value, _key, item) => {
              return {
                submitted: item.submitted ? new Date(Date.parse(item.submitted + "Z")) : null,
                started: item.started ? new Date(Date.parse(item.started + "Z")) : null,
                finished: item.finished ? new Date(Date.parse(item.finished + "Z")) : null,
              };
            },
            tdClass: ["text-center"],
          },
          {
            key: "progress",
            sortable: false,
            formatter: (_value, _key, item) => {
              let percentage;
              if (item.total_results === 0) {
                percentage = "0.00%";
              } else if (item.status === "FINISHED" || item.processed_results > item.total_results) {
                percentage = "100.00%";
              } else {
                percentage = `${((item.processed_results / item.total_results) * 100).toFixed(2)}%`;
              }

              return {
                status: item.status,
                percentage: percentage,
                processed: item.processed_results,
                total: item.total_results,
              };
            },
          },
          {
            key: "size",
            sortable: true,
            formatter: this.formatBytes,
            tdClass: ["text-right", "text-nowrap"],
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
  head() {
    return {
      title: "Dashboard",
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/view/dashboard.sass" />
