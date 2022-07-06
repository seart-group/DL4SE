<template>
  <div class="paginated-table-container">
    <b-table :id="id" class="paginated-table-border" borderless responsive
             table-class="paginated-table" head-variant="dark" sticky-header="370px"
             thead-class="paginated-table-header" thead-tr-class="paginated-table-header-row"
             tbody-class="paginated-table-body" tbody-tr-class="paginated-table-row" hover
             show-empty :items="provider" :api-url="apiEndpoint"
             :primary-key="primaryKey" :fields="fields" sort-icon-left
             :per-page="perPage" :current-page="currentPage"
             v-bind="$attrs" v-on="$listeners"
    >
      <template v-for="(_, scopedSlotName) in $scopedSlots" v-slot:[scopedSlotName]="slotData">
        <slot :name="scopedSlotName" v-bind="slotData" />
      </template>
      <template v-for="(_, slotName) in $slots" v-slot:[slotName]>
        <slot :name="slotName" />
      </template>
    </b-table>
    <b-container class="paginated-table-controls">
      <b-row no-gutters align-h="center">
        <b-col md="auto" cols="12">
          <b-pagination v-model="currentPage"
                        :per-page="perPage"
                        :total-rows="totalRows"
                        last-number first-number
                        align="center"
          >
            <template #prev-text><b-icon-chevron-left /></template>
            <template #next-text><b-icon-chevron-right /></template>
            <template #ellipsis-text><b-icon-three-dots /></template>
          </b-pagination>
        </b-col>
        <b-col md="auto" col>
          <b-dropdown-select header="Choose Page Size" placeholder="Page Size"
                             v-model="perPage" :options="perPageOptions"
                             class="mb-3"
          />
        </b-col>
        <b-col cols="auto">
          <b-button class="paginated-table-refresh" @click="refresh">
            <b-icon-arrow-clockwise shift-h="-2" rotate="45" />
          </b-button>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import BDropdownSelect from "@/components/DropdownSelect"

export default {
  name: "b-paginated-table",
  components: { BDropdownSelect },
  props: {
    id: String,
    fields: {
      type: Array,
      default() {
        return []
      }
    },
    primaryKey: {
      type: String,
      required: false
    },
    apiEndpoint: {
      type: String,
      required: true
    },
    refreshRate: {
      type: Number,
      default: -1
    }
  },
  methods: {
    async provider(ctx) {
      const params = {
        page: ctx.currentPage,
        size: ctx.perPage
      }
      if (ctx.sortBy) {
        params.sort = `${ctx.sortBy},${(ctx.sortDesc) ? "desc" : "asc"}`
      }
      const config = {
        params: params,
        headers: { 'authorization': this.$store.getters.getToken }
      }
      return this.$http.get(ctx.apiUrl, config).then(res => {
        this.totalRows = res.data["total_items"]
        return res.data.items
      })
    },
    refresh() {
      this.$root.$emit('bv::refresh::table', this.id)
    }
  },
  mounted() {
    if (this.refreshRate >= 0) {
      setInterval(this.refresh, this.refreshRate)
    }
  },
  data() {
    return {
      currentPage: 1,
      perPage: 20,
      perPageOptions: [ 10, 20, 50, 100 ],
      totalRows: 0
    }
  }
}
</script>