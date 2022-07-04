<template>
  <div id="dashboard">
    <h1 class="page-title">Dashboard</h1>
    <b-container>
      <b-row>
        <b-col>
          <h3 class="mb-3">Tasks</h3>
        </b-col>
      </b-row>
      <b-row align-h="center">
        <b-col>
          <b-paginated-table :id="taskTable.id"
                             :api-url="taskTable.apiUrl"
                             :fields="taskTable.fields"
                             :primary-key="taskTable.fields[0].key"
          >
            <template #cell(uuid)="row">
              <span v-html="row.value" class="text-nowrap" />
            </template>
            <template #cell(status)="row">
              <div class="d-flex justify-content-center">
                <b-icon :icon="statusToSquareIcon(row.value)"
                        v-b-tooltip="toTitle(row.value)"
                        font-scale="1.25" class="align-middle"
                />
              </div>

            </template>
            <template #cell(timeline)="row">
              <div class="d-lg-table-cell d-inline-flex">
                <template v-if="row.value.submitted">
                  <b-icon-calendar-plus v-b-tooltip.html="`Submitted at:<br />${row.value.submitted.toISOString()}`"
                                        font-scale="1.35" class="align-middle"
                  />
                </template>
                <template v-if="row.value.started">
                  <b-icon-dash-lg shift-v="-3" />
                  <b-icon-calendar-play v-b-tooltip.html="`Started at:<br />${row.value.started.toISOString()}`"
                                        font-scale="1.35" class="align-middle"
                  />
                </template>
                <template v-if="row.value.finished">
                  <b-icon-dash-lg shift-v="-3" />
                  <component :is="statusToCalendarIcon(row.item.status)"
                             v-b-tooltip.html="`${toTitle(row.item.status)} at:<br />
                                                ${row.value.finished.toISOString()}`"
                             font-scale="1.35" class="align-middle"
                  />
                </template>
              </div>
            </template>
            <template #cell(progress)="row">
              <div class="d-flex flex-column text-center">
                <span v-html="row.value.percentage" />
                <b-progress :max="row.value.total" :value="row.value.processed"
                            v-b-tooltip.html="`Total Instances:<br />${row.value.total}`"
                            variant="dark" class="border-secondary" tabindex="0"
                />
              </div>
            </template>
            <template #cell(details)="row">
              <div class="d-lg-table-cell d-inline-flex">
                <b-button class="action-btn mr-1" size="sm"
                          v-b-tooltip="'Show User Details'"
                          @click="display('Submitter', row.item.user, $event.target)"
                >
                  <b-icon-person-lines-fill />
                </b-button>
                <b-button class="action-btn mr-1" size="sm"
                          v-b-tooltip="'Show Query Details'"
                          @click="display('Query', row.item.query, $event.target)"
                >
                  <b-icon-search />
                </b-button>
                <b-button class="action-btn" size="sm"
                          v-b-tooltip="'Show Processing Details'"
                          @click="display('Processing', row.item.processing, $event.target)"
                >
                  <b-icon-gear-fill />
                </b-button>
              </div>
            </template>
            <template #cell(actions)="row">
              <div class="d-lg-table-cell d-inline-flex">
                <span class="d-inline-block mr-1" tabindex="0" v-b-tooltip="'Cancel Task'">
                  <b-button class="action-btn" size="sm"
                            :disabled="[ 'FINISHED', 'CANCELLED', 'ERROR' ].includes(row.item.status)"
                            @click="taskCancel(row.item.uuid)"
                  >
                    <b-icon-trash />
                  </b-button>
                </span>
                <span class="d-inline-block mr-1" tabindex="0" v-b-tooltip="'Edit Task'">
                  <b-button class="action-btn" size="sm"
                            :to="{ name: 'task', params: { uuid: row.item.uuid } }"
                  >
                    <b-icon-pencil-square />
                  </b-button>
                </span>
                <span class="d-inline-block" tabindex="0" v-b-tooltip="'Download Results'">
                  <b-button class="action-btn" size="sm"
                            :to="{ name: 'download', params: { uuid: row.item.uuid } }"
                            :disabled="(row.item.status !== 'FINISHED') || row.item.expired"
                  >
                    <b-icon-download />
                  </b-button>
                </span>
              </div>
            </template>
          </b-paginated-table>
        </b-col>
      </b-row>
    </b-container>
    <b-container>
      <b-row>
        <b-col>
          <h3 class="mb-3">Users</h3>
        </b-col>
      </b-row>
      <b-row align-h="center">
        <b-col>
          <b-paginated-table :id="userTable.id"
                             :api-url="userTable.apiUrl"
                             :fields="userTable.fields"
                             :primary-key="userTable.fields[0].key"
          >
            <template #cell(registered)="row">
              <b-abbreviation :value="row.value.toISOString()" :transformer="(iso) => iso.split('T')[0]" />
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
                <b-button class="action-btn mr-1" size="sm"
                          v-b-tooltip="row.item.enabled ? 'Disable' : 'Enable'"
                          @click="userAction(row.item.uid, row.item.enabled ? 'disable' : 'enable')"
                >
                  <b-icon :icon="`person-${row.item.enabled ? 'x' : 'check'}-fill`" />
                </b-button>
                <b-button class="action-btn" size="sm"
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
    <b-container>
      <b-row align-h="center">
        <b-col cols="auto">
          <b-button to="task" class="action-btn">Create Task</b-button>
        </b-col>
      </b-row>
    </b-container>
    <b-modal :id="detailsModal.id" :title="detailsModal.title"
             content-class="rounded-0" scrollable centered
             footer-class="justify-content-start"
             @hidden="reset"
    >
      <pre :id="detailsModal.id+'-content'" class="m-0">{{ detailsModal.content }}</pre>
      <template #modal-footer>
        <b-button :id="detailsModal.id+'-btn'" class="action-btn" @click="copy">
          <b-icon-clipboard />
        </b-button>
        <b-tooltip :target="detailsModal.id+'-btn'"
                   :show.sync="detailsModal.showTooltip"
                   @shown="autoHideTooltip" triggers="click"
                   title="Copied!"
        />
      </template>
    </b-modal>
  </div>
</template>

<script>
import axios from "axios"
import BAbbreviation from "@/components/Abbreviation"
import BIconCalendarExclamation from "@/components/IconCalendarExclamation"
import BIconCalendarPlay from "@/components/IconCalendarPlay"
import BIconCalendarQuestion from "@/components/IconCalendarQuestion"
import BPaginatedTable from "@/components/PaginatedTable"

export default {
  components: {
    BAbbreviation,
    BIconCalendarExclamation,
    BIconCalendarPlay,
    BIconCalendarQuestion,
    BPaginatedTable
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
    async taskCancel(uuid) {
      const url = `https://localhost:8080/api/task/cancel/${uuid}`
      const config = {
        headers: { 'authorization': this.$store.getters.getToken }
      }
      await axios.post(url, null, config).then(() => {
        this.$root.$emit("bv::refresh::table", this.taskTable.id)
      }).catch(console.log)
      // TODO 23.06.22: Better error handling
    },
    async userAction(uid, action) {
      const url = `https://localhost:8080/api/admin/user/${uid}/${action}`
      const config = {
        headers: { 'authorization': this.$store.getters.getToken }
      }
      await axios.post(url, null, config).then(() => {
        this.$root.$emit("bv::refresh::table", this.userTable.id)
      }).catch(console.log)
      // TODO 23.06.22: Better error handling
    },
    display(title, item, button) {
      this.detailsModal.title = title
      this.detailsModal.content = JSON.stringify(item, null, 2)
      this.$root.$emit('bv::show::modal', this.detailsModal.id, button)
    },
    reset() {
      this.detailsModal.title = ""
      this.detailsModal.content = ""
      this.detailsModal.showTooltip = false
    },
    copy() {
      navigator.clipboard.writeText(this.detailsModal.content)
          .then(() => this.detailsModal.showTooltip = true)
    },
    autoHideTooltip() {
      setTimeout(() => this.detailsModal.showTooltip = false, 2000)
    }
  },
  data() {
    return {
      detailsModal: {
        id: "details-modal",
        title: "",
        content: "",
        showTooltip: false
      },
      taskTable: {
        id: "task-table",
        apiUrl: "https://localhost:8080/api/admin/task",
        fields: [
          {
            key: "uuid",
            label: "UUID",
            sortable: true
          },
          {
            key: "status",
            sortable: true
          },
          {
            key: "timeline",
            sortable: false,
            formatter: (_value, _key, item) => {
              return {
                submitted: (item.submitted) ? new Date(Date.parse(item.submitted + 'Z')) : null,
                started: (item.started) ? new Date(Date.parse(item.started + 'Z')) : null,
                finished: (item.finished) ? new Date(Date.parse(item.finished + 'Z')) : null
              }
            }
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
                percentage: percentage,
                processed: item.processed_results,
                total: item.total_results
              }
            }
          },
          {
            key: "details",
            sortable: false
          },
          {
            key: "actions",
            sortable: false
          }
        ]
      },
      userTable: {
        id: "user-table",
        apiUrl: "https://localhost:8080/api/admin/user",
        fields: [
          {
            key: "uid",
            label: "UID",
            sortable: true
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
        ]
      }
    }
  }
}
</script>
