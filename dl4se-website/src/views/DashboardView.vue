<template>
  <div id="dashboard">
    <h1 class="page-title">Dashboard</h1>
    <b-container>
      <b-row>
        <b-col>
          <h3 class="mb-3">Platform Users</h3>
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
              <b-iconstack v-b-tooltip="(row.item.enabled ? '' : 'Disabled ') + toTitleCase(row.item.role)" scale="1.35">
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
      <pre :id="detailsModal.id+'-content'" class="m-0" v-html="detailsModal.content" />
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
import BAbbreviation from "@/components/Abbreviation";
import BPaginatedTable from "@/components/PaginatedTable"

export default {
  components: { BAbbreviation, BPaginatedTable },
  methods: {
    toTitleCase(str) {
      return str.replace(
          /\w\S*/g,
          (txt) => txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase()
      )
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
          }
        ]
      }
    }
  }
}
</script>
