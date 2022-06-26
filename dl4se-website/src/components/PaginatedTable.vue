<template>
  <div class="paginated-table-container">
    <b-table :id="id" borderless responsive hover sticky-header="290px"
             table-class="paginated-table" head-variant="dark"
             thead-class="paginated-table-header" thead-tr-class="paginated-table-header-row"
             tbody-class="paginated-table-body" tbody-tr-class="paginated-table-row"
             show-empty :items="provider" :api-url="apiUrl"
             :primary-key="primaryKey" :fields="allFields" sort-icon-left
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
    <div class="paginated-table-controls">
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
      <b-dropdown-select header="Choose Page Size" placeholder="Page Size"
                         v-model="perPage" :options="perPageOptions"
                         class="mb-3"
      />
    </div>
  </div>
</template>

<script>
import BDropdownSelect from "@/components/DropdownSelect";
import axios from "axios";

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
    apiUrl: {
      type: String,
      required: true
    },
    itemTransformer: {
      type: Function,
      default(value) {
        return value
      }
    }
  },
  computed: {
    allFields() {
      return [ ...this.fields, { key: "details", sortable: false }, { key: "actions", sortable: false } ]
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
      return axios.get(ctx.apiUrl, config).then(res => {
        this.totalRows = res.data["total_items"]
        res.data.items.forEach(this.itemTransformer)
        return res.data.items
      })
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