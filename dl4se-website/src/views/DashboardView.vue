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
              <span :title="row.unformatted">{{ row.value }}</span>
            </template>
            <template #cell(details)="row">
              <b-icon-patch-check-fill v-if="row.item.verified" v-b-tooltip="'Email Verified'" class="mr-1" />
              <b-icon-patch-question-fill v-else v-b-tooltip="'Email Unverified'" class="mr-1" />
              <b-iconstack v-b-tooltip="(row.item.enabled ? '' : 'Disabled ') + toTitleCase(row.item.role)">
                <b-icon-person-plus-fill stacked v-if="row.item.role === 'ADMIN'" shift-h="2" />
                <b-icon-person-fill stacked v-else />
                <b-icon-x-circle v-if="!row.item.enabled" stacked variant="danger" />
              </b-iconstack>
            </template>
            <template #cell(actions)="row">
              <div class="d-lg-table-cell d-inline-flex">
                <b-button class="action-btn mr-1" size="sm"
                          v-b-tooltip="!row.item.enabled ? 'Enable' : 'Disable'"
                          @click="userAction(row.item.uid, !row.item.enabled ? 'enable' : 'disable')"
                >
                  <b-icon-person-check-fill v-if="!row.item.enabled" />
                  <b-icon-person-x-fill v-else />
                </b-button>
                <b-button class="action-btn" size="sm"
                          v-b-tooltip="row.item.role === 'ADMIN' ? 'Demote' : 'Promote'"
                          @click="userAction(row.item.uid, row.item.role === 'ADMIN' ? 'demote' : 'promote')"
                >
                  <b-icon-person-dash-fill v-if="row.item.role === 'ADMIN'" />
                  <b-icon-person-plus-fill v-else />
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
  </div>
</template>

<script>
import axios from "axios"
import BPaginatedTable from "@/components/PaginatedTable"

export default {
  components: { BPaginatedTable },
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
        this.$root.$emit("bv::refresh::table", "user-table")
      }).catch(console.log)
      // TODO 23.06.22: Better error handling
    }
  },
  data() {
    return {
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
            formatter: (value) => value.split('T')[0]
          }
        ]
      }
    }
  }
}
</script>
