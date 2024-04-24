<template>
  <div id="dashboard" v-if="show">
    <h1 class="page-title">Dashboard</h1>
    <b-container>
      <b-row align-h="center">
        <b-col>
          <b-paginated-table :id="taskTable.id"
                             title="Requested Datasets"
                             :fields="taskTable.fields"
                             :controls="[ 'modal', 'filters' ]"
                             :primary-key="taskTable.fields[0].key"
                             :total-items="taskTable.totalItems"
                             :provider="taskProvider"
                             :sticky-header="tableHeight"
          >
            <template #controls(modal)>
              <b-button v-b-modal.dataset-select block class="paginated-table-btn">
                <b-icon-plus class="align-middle" font-scale="1.5" />
                <span class="align-middle">Create New Dataset</span>
              </b-button>
            </template>
            <template #controls(filters)>
              <b-button v-b-modal.task-filter-select block class="paginated-table-btn">
                <b-icon-filter class="align-middle" font-scale="1.5" />
                <span class="align-middle">Filter Settings</span>
              </b-button>
            </template>
            <template #cell(uuid)="row">
              <b-abbreviation v-if="!$screen.md"
                              :value="row.value"
                              :formatter="val => val.split('-')[0]"
              />
              <span v-html="row.value" v-else />
            </template>
            <template #cell(status)="row">
              <b-icon :icon="statusToSquareIcon(row.value)"
                      v-b-tooltip="toTitle(row.value)"
                      font-scale="1.25"
              />
            </template>
            <template #cell(submitted)="row">
              <div class="d-inline-flex align-items-center">
                <template v-if="row.value.submitted">
                  <b-icon-calendar-plus v-b-tooltip.html="`Submitted at:<br />${row.value.submitted.toISOString()}`"
                                        font-scale="1.35"
                  />
                </template>
                <template v-if="row.value.started">
                  <b-icon-dash-lg shift-v="-1" />
                  <b-icon-calendar-play v-b-tooltip.html="`Started at:<br />${row.value.started.toISOString()}`"
                                        font-scale="1.35"
                  />
                </template>
                <template v-else>
                  <b-icon-blank />
                  <b-icon-blank font-scale="1.35" />
                </template>
                <template v-if="row.value.finished">
                  <b-icon-dash-lg shift-v="-1" />
                  <component :is="statusToCalendarIcon(row.item.status)"
                             v-b-tooltip.html="`${toTitle(row.item.status)} at:<br />${row.value.finished.toISOString()}`"
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
                  <span v-html="row.value.percentage" />
                  <b-progress :max="row.value.total" :value="row.value.processed"
                              v-b-tooltip.html="`Total Instances:<br />${row.value.total}`"
                              variant="dark" class="border-secondary" tabindex="0"
                  />
                </template>
              </div>
            </template>
            <template #cell(details)="row">
              <div class="d-inline-flex">
                <b-button class="btn-secondary-border-2 mr-1" size="sm"
                          v-b-tooltip="'Show User Details'"
                          @click="display('Submitter', row.item.user, $event.target)"
                >
                  <b-icon-person-lines-fill />
                </b-button>
                <b-button class="btn-secondary-border-2 mr-1" size="sm"
                          v-b-tooltip="'Show Query Details'"
                          @click="display('Query', row.item.query, $event.target)"
                >
                  <b-icon-search />
                </b-button>
                <b-button class="btn-secondary-border-2" size="sm"
                          v-b-tooltip="'Show Processing Details'"
                          @click="display('Processing', row.item.processing, $event.target)"
                >
                  <b-icon-gear-fill />
                </b-button>
              </div>
            </template>
            <template #cell(actions)="row">
              <div class="d-inline-flex">
                <template v-if="[ 'FINISHED', 'CANCELLED', 'ERROR' ].includes(row.item.status)">
                  <span class="d-inline-block mr-1" tabindex="0" v-b-tooltip="'Cancel Task'">
                    <b-button class="btn-secondary-border-2" size="sm" disabled>
                      <b-icon-trash />
                    </b-button>
                  </span>
                </template>
                <template v-else>
                  <b-button class="btn-secondary-border-2 mr-1" size="sm"
                            v-b-tooltip="'Cancel Task'"
                            @click="taskCancel(row.item.uuid)"
                  >
                    <b-icon-trash />
                  </b-button>
                </template>
                <b-button class="btn-secondary-border-2 mr-1" size="sm"
                          :to="{ name: 'code-regular', params: { uuid: row.item.uuid } }"
                          v-b-tooltip="'Edit Task'"
                >
                  <b-icon-pencil-square />
                </b-button>
                <template v-if="(row.item.status !== 'FINISHED') || row.item.expired || row.item.total_results === 0">
                  <span class="d-inline-block" tabindex="0" v-b-tooltip="'Download Results'">
                    <b-button class="btn-secondary-border-2" size="sm" disabled>
                      <b-icon-download />
                    </b-button>
                  </span>
                </template>
                <template v-else>
                  <b-button class="btn-secondary-border-2 d-inline-block" size="sm"
                            :to="{ name: 'download', params: { uuid: row.item.uuid } }"
                            v-b-tooltip="'Download Results'"
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
    <b-container v-if="isAdmin">
      <b-row align-h="center">
        <b-col>
          <b-paginated-table :id="userTable.id"
                             title="Platform Users"
                             :fields="userTable.fields"
                             :controls="[ 'filters' ]"
                             :primary-key="userTable.fields[0].key"
                             :total-items="userTable.totalItems"
                             :provider="userProvider"
                             :sticky-header="tableHeight"
          >
            <template #controls(filters)>
              <b-button v-b-modal.user-filter-select block class="paginated-table-btn">
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
              <b-icon :icon="`patch-${row.item.verified ? 'check' : 'question'}-fill`"
                      v-b-tooltip="`Email ${row.item.verified ? 'Verified' : 'Unverified'}`"
                      class="mr-2" scale="1.35"
              />
              <b-iconstack v-b-tooltip="(row.item.enabled ? '' : 'Disabled ') + toTitle(row.item.role)" scale="1.35">
                <b-icon :icon="(row.item.role === 'ADMIN') ? 'person-plus-fill' : 'person-fill'"
                        :shift-h="(row.item.role === 'ADMIN') ? 2 : 0"
                        stacked
                />
                <b-icon icon="x-circle" stacked variant="danger" v-if="!row.item.enabled" />
              </b-iconstack>
            </template>
            <template #cell(actions)="row">
              <div class="d-lg-table-cell d-inline-flex">
                <b-button class="btn-secondary-border-2 mr-1" size="sm"
                          v-b-tooltip="row.item.enabled ? 'Disable' : 'Enable'"
                          @click="userAction(row.item.uid, row.item.enabled ? 'disable' : 'enable')"
                >
                  <b-icon :icon="`person-${row.item.enabled ? 'x' : 'check'}-fill`" />
                </b-button>
                <b-button class="btn-secondary-border-2" size="sm"
                          v-b-tooltip="(row.item.role === 'ADMIN') ? 'Demote' : 'Promote'"
                          @click="userAction(row.item.uid, (row.item.role === 'ADMIN') ? 'demote' : 'promote')"
                >
                  <b-icon :icon="`person-${(row.item.role === 'ADMIN') ? 'dash' : 'plus'}-fill`" />
                </b-button>
              </div>
            </template>
          </b-paginated-table>
        </b-col>
      </b-row>
    </b-container>
    <b-container v-if="isAdmin">
      <h3>Server Log</h3>
      <b-monitor :supplier="getLog" />
    </b-container>
    <b-container v-if="isAdmin">
      <h3>Server Environment</h3>
      <b-config-table :supplier="getConfiguration"
                      :consumer="updateConfiguration"
      />
    </b-container>
    <b-container v-if="isAdmin">
      <h3>Server Controls</h3>
      <b-content-area class="d-flex justify-content-md-start justify-content-around">
        <b-button @click="shutdownServer" class="btn-danger-border-2 mr-md-2">
          <b-icon-power />
          Shutdown
        </b-button>
        <b-button @click="restartServer" class="btn-secondary-border-2 ml-md-2">
          <b-icon-arrow-clockwise shift-h="-2" rotate="45" />
          Restart
        </b-button>
      </b-content-area>
    </b-container>
    <b-details-modal :id="detailsModal.id"
                     :title="detailsModal.title"
                     :content="detailsModal.content"
                     :formatters="detailsModal.formatters"
                     @reset="detailsModal.title = ''; detailsModal.content = {}"
    />
    <b-dialog-modal id="dataset-select" title="Select Dataset">
      <b-button block :to="{ name: 'code-generic' }" class="dialog-modal-btn">Generic Code Dataset</b-button>
      <b-button block :to="{ name: 'code-regular' }" class="dialog-modal-btn">Code Completion Dataset</b-button>
    </b-dialog-modal>
    <b-dialog-modal id="task-filter-select"
                    title="Specify Task Filters"
                    @hide="$root.$emit('bv::refresh::table', taskTable.id)"
    >
      <label for="task-filter-uuid">
        Filter by UUID:
      </label>
      <b-clearable-input id="task-filter-uuid"
                         placeholder="XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"
                         v-model="taskTable.filters.uuid"
      />
    </b-dialog-modal>
    <b-dialog-modal id="user-filter-select"
                    title="Specify User Filters"
                    @hide="$root.$emit('bv::refresh::table', userTable.id)"
    >
      <label for="user-filter-uid">
        Filter by UID:
      </label>
      <b-clearable-input id="user-filter-uid"
                         placeholder="XXXXXXXXXXX"
                         v-model="userTable.filters.uid"
                         class="mb-3"
      />
      <label for="user-filter-email">
        Filter by Email:
      </label>
      <b-clearable-input id="user-filter-email"
                         placeholder="example@email.com"
                         v-model="userTable.filters.email"
                         class="mb-3"
      />
      <label for="user-filter-organisation">
        Filter by Organisation:
      </label>
      <b-clearable-input id="user-filter-organisation"
                         placeholder=""
                         v-model="userTable.filters.organisation"
      />
    </b-dialog-modal>
  </div>
</template>

<script>
import bootstrapMixin from "@/mixins/bootstrapMixin"
import formatterMixin from "@/mixins/formatterMixin"
import routerMixin from "@/mixins/routerMixin"
import BAbbreviation from "@/components/Abbreviation"
import BClearableInput from "@/components/ClearableInput"
import BConfigTable from "@/components/ConfigTable";
import BContentArea from "@/components/ContentArea"
import BDetailsModal from "@/components/DetailsModal"
import BDialogModal from "@/components/DialogModal"
import BIconCalendarExclamation from "@/components/IconCalendarExclamation"
import BIconCalendarPlay from "@/components/IconCalendarPlay"
import BIconCalendarQuestion from "@/components/IconCalendarQuestion"
import BIconIdenticon from "@/components/IconIdenticon"
import BMonitor from "@/components/Monitor"
import BPaginatedTable from "@/components/PaginatedTable"

export default {
  components: {
    BAbbreviation,
    BClearableInput,
    BConfigTable,
    BContentArea,
    BDetailsModal,
    BDialogModal,
    BIconCalendarExclamation,
    BIconCalendarPlay,
    BIconCalendarQuestion,
    BIconIdenticon,
    BMonitor,
    BPaginatedTable
  },
  mixins: [ bootstrapMixin, formatterMixin, routerMixin ],
  computed: {
    tableHeight() {
      return `${this.$screen.xl ? 370 : 380}px`
    }
  },
  methods: {
    toTitle(value) {
      return this.$_.startCase(
          this.$_.lowerCase(
              this.$_.defaultTo(
                  value, "???"
              )
          )
      )
    },
    plaintextFormatter(item) {
      return Object.entries(item)
          .filter(([, value]) => {
            switch (typeof value) {
              case "string":
              case "object":
                return value?.length
              default:
                return Boolean(value)
            }
          })
          .sort(([key1, value1], [key2, value2]) => {
            const order = [ "boolean", "number", "string", "object" ]
            const type1 = this.$_.indexOf(order, typeof value1)
            const type2 = this.$_.indexOf(order, typeof value2)
            if (type1 < type2) return 1
            if (type1 > type2) return -1
            else return key2.localeCompare(key1)
          })
          .map(([key, value]) => {
            const label = this.$_.startCase(key)
            switch (typeof value) {
              case "boolean":
                return `- ${label}`
              case "object":
                if (value instanceof Array) {
                  const array = value.map(v => `  - ${v}`).join("\n")
                  return `- ${label}:\n${array}`
                } else {
                  const object = this.plaintextFormatter(value)
                  const indented = object.split("\n").map(line => `  ${line}`).join("\n")
                  return `- ${label}:\n${indented}`
                }
              default:
                return `- ${label}: ${value}`
            }
          })
          .join("\n")
    },
    statusToSquareIcon(status) {
      switch (status) {
        case 'QUEUED': return "plus-square-fill"
        case 'EXECUTING': return "caret-right-square-fill"
        case 'FINISHED': return "check-square-fill"
        case 'CANCELLED': return "x-square-fill"
        case 'ERROR': return "exclamation-square-fill"
        default: return "question-square-fill"
      }
    },
    statusToCalendarIcon(status) {
      switch (status) {
        case 'QUEUED': return "b-icon-calendar-plus"
        case 'EXECUTING': return "b-icon-calendar-play"
        case 'FINISHED': return "b-icon-calendar-check"
        case 'CANCELLED': return "b-icon-calendar-x"
        case 'ERROR': return "b-icon-calendar-exclamation"
        default: return "b-icon-calendar-question"
      }
    },
    async taskProvider(ctx) {
      const params = { page: ctx.currentPage, size: ctx.perPage }
      if (ctx.sortBy) params.sort = `${ctx.sortBy},${ctx.sortDesc ? "desc" : "asc"}`
      const filters = this.taskTable.filters
      if (filters.uuid) params.uuid = filters.uuid
      return this.$http.get("/task", { params: params })
          .then((res) => {
            this.taskTable.totalItems = res.data.total_items
            return res.data.items
          }).catch(() => {
            this.appendToast(
                "Error Fetching Task Data",
                "There was a problem retrieving the task data. Refresh the page and try again.",
                "warning"
            )
          })
    },
    async userProvider(ctx) {
      const params = { page: ctx.currentPage, size: ctx.perPage }
      if (ctx.sortBy) params.sort = `${ctx.sortBy},${ctx.sortDesc ? "desc" : "asc"}`
      const filters = this.userTable.filters
      if (filters.uid) params.uid = filters.uid
      if (filters.email) params.email = filters.email
      if (filters.organisation) params.organisation = filters.organisation
      return this.$http.get("/admin/user", { params: params })
          .then((res) => {
            this.userTable.totalItems = res.data.total_items
            return res.data.items
          }).catch(() => {
            this.appendToast(
                "Error Fetching User Data",
                "There was a problem retrieving the user data. Refresh the page and try again.",
                "warning"
            )
          })
    },
    async taskCancel(uuid) {
      const endpoint = `/task/${uuid}/cancel`
      await this.$http.post(endpoint)
          .catch((err) => {
            const status = err.response.status
            switch (status) {
              case 400:
                this.appendToast(
                    "Cannot cancel task",
                    "The task has already finished executing and can not be cancelled.",
                    "secondary"
                )
                break
              case 401:
                this.$store.dispatch("logOut").then(() => {
                  this.appendToast(
                      "Login Required",
                      "Your session has expired. Please log in again.",
                      "secondary"
                  )
                })
                break
              case 403:
                this.$store.dispatch("logOut").then(() => {
                  this.appendToast(
                      "Access Restricted",
                      "You do not have the necessary authorization to modify the requested resource.",
                      "secondary"
                  )
                })
                break
              default:
                this.$router.push({ name: 'home' })
                break
            }
          })
      this.$root.$emit("bv::refresh::table", this.taskTable.id)
    },
    async userAction(uid, action) {
      const endpoint = `/admin/user/${uid}/${action}`
      await this.$http.post(endpoint)
          .catch((err) => {
            const status = err.response.status
            switch (status) {
              case 401:
                this.$store.dispatch("logOut").then(() => {
                  this.appendToast(
                      "Login Required",
                      "Your session has expired. Please log in again.",
                      "secondary"
                  )
                })
                break
              case 403:
                this.$store.dispatch("logOut").then(() => {
                  this.appendToast(
                      "Access Restricted",
                      "You do not have the necessary authorization to modify the requested resource.",
                      "secondary"
                  )
                })
                break
              default:
                this.$router.push({ name: 'home' })
                break
            }
          })
      this.$root.$emit("bv::refresh::table", this.userTable.id)
    },
    // TODO 21.10.22: Not what I would call "full-proof" but it will work for now
    cleanLog(str) {
      const lines = this.$_.split(str, /[\n\r]/)
      const regex = /^\d{4}-\d{2}-\d{2}\s\d{2}:\d{2}:\d{2}.\d{3}\s+(TRACE|DEBUG|INFO|WARN|ERROR)\s+\d+\s-{3}/
      if (!regex.test(lines[0])) lines.shift()
      return lines.join("\n")
    },
    async getLog() {
      const endpoint = "/actuator/logfile"
      const config = { headers: { "Range": "bytes=-1048576" }}
      return this.$http.get(endpoint, config)
          .then((res) => res.data)
          .then(this.cleanLog)
    },
    async getConfiguration() {
      const endpoint = "/admin/configuration"
      return this.$http.get(endpoint).then((res) => res.data)
    },
    async updateConfiguration(configuration) {
      const endpoint = "/admin/configuration"
      return this.$http.post(endpoint, configuration).then((res) => res.data)
    },
    async shutdownServer() {
      this.showConfirmModal(
          "Shut Down Server",
          "You are about to shut down the server. " +
          "Doing so will cause any currently executing tasks to be suspended, " +
          "and the API unavailable until it is brought back up. " +
          "Are you sure you want to continue?"
      ).then((confirmed) => {
        if (confirmed) {
          return this.$http.post("/actuator/shutdown")
        } else {
          return Promise.reject()
        }
      }).then(() => {
        this.redirectHomeAndToast(
            "Shutting Down Server",
            "The server has been successfully shut down.",
            "secondary"
        )
      }).catch(() => {})
      // TODO 20.10.22: Display failure toast
    },
    async restartServer() {
      const restarted = await this.showConfirmModal(
          "Restart Server",
          "You are about to restart the server. " +
          "Doing so will cause any currently executing tasks to be temporarily suspended. " +
          "During this time the API will also be unavailable. " +
          "Are you sure you want to continue?"
      ).then((confirmed) => {
        if (confirmed) {
          return this.$http.post("/actuator/restart")
        } else {
          return Promise.reject(false)
        }
      }).then(() => {
        this.appendToast(
            "Restarting Server",
            "Server restart has been initiated. It may take a moment before it becomes available again.",
            "secondary"
        )
        return true
      }).catch(() => {
        return false
      })

      if (!restarted) return
      const that = this
      const check = setInterval(async function() {
        await that.$http.get("/")
            .then(() => {
              clearInterval(check)
              that.appendToast(
                  "Server Connection Restored",
                  "The DL4SE server is back online.",
                  "secondary"
              )
            })
            .catch(() => {})
      }, 500)
    },
    display(title, item, button) {
      this.detailsModal.title = title
      this.detailsModal.content = item
      this.$root.$emit('bv::show::modal', this.detailsModal.id, button)
    }
  },
  async beforeMount() {
    this.isAdmin = await this.$http.get("/admin")
        .then(() => true)
        .catch(() => false)
    this.show = true
  },
  data() {
    return {
      show: false,
      isAdmin: undefined,
      detailsModal: {
        id: "details-modal",
        title: "",
        content: "",
        formatters: [
          {
            name: "JSON",
            formatter: item => JSON.stringify(item, null, 2)
          },
          {
            name: "Plaintext",
            formatter: this.plaintextFormatter
          }
        ]
      },
      taskTable: {
        id: "task-table",
        filters: {
          uuid: null,
        },
        fields: [
          {
            key: "uuid",
            label: "UUID",
            sortable: true,
            tdClass: [ "text-nowrap", "text-monospace" ]
          },
          {
            key: "status",
            sortable: true,
            tdClass: [ "text-center" ]
          },
          {
            key: "submitted",
            label: "Timeline",
            sortable: true,
            formatter: (_value, _key, item) => {
              return {
                submitted: (item.submitted) ? new Date(Date.parse(item.submitted + 'Z')) : null,
                started: (item.started) ? new Date(Date.parse(item.started + 'Z')) : null,
                finished: (item.finished) ? new Date(Date.parse(item.finished + 'Z')) : null
              }
            },
            tdClass: [ "text-center" ]
          },
          {
            key: "progress",
            sortable: false,
            formatter: (_value, _key, item) => {
              let percentage
              if (item.total_results === 0) {
                percentage = "0.00%"
              } else if (item.status === 'FINISHED' || item.processed_results > item.total_results) {
                percentage = "100.00%"
              } else {
                percentage = `${((item.processed_results / item.total_results) * 100).toFixed(2)}%`
              }

              return {
                status: item.status,
                percentage: percentage,
                processed: item.processed_results,
                total: item.total_results
              }
            }
          },
          {
            key: "size",
            sortable: true,
            formatter: this.formatBytes,
            tdClass: [ "text-right", "text-nowrap" ]
          },
          {
            key: "details",
            sortable: false
          },
          {
            key: "actions",
            sortable: false
          }
        ],
        totalItems: 0
      },
      userTable: {
        id: "user-table",
        filters: {
          uid: null,
          email: null,
          organisation: null
        },
        fields: [
          {
            key: "uid",
            label: "UID",
            sortable: true,
            tdClass: [ "text-monospace", "text-nowrap" ]
          },
          {
            key: "email",
            sortable: true
          },
          {
            key: "organisation",
            sortable: true
          },
          {
            key: "registered",
            sortable: true,
            formatter: (value) => new Date(Date.parse(value + 'Z'))
          },
          {
            key: "details",
            sortable: false
          },
          {
            key: "actions",
            sortable: false
          }
        ],
        totalItems: 0
      }
    }
  }
}
</script>
