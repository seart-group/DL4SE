<template>
  <div class="paginated-table-container">
    <h3 v-if="title">{{ title }}</h3>
    <b-table
      :id="id"
      class="paginated-table-border"
      borderless
      responsive
      table-class="paginated-table"
      head-variant="dark"
      thead-class="paginated-table-header"
      thead-tr-class="paginated-table-header-row"
      tbody-class="paginated-table-body"
      tbody-tr-class="paginated-table-row"
      hover
      show-empty
      :items="provider"
      :primary-key="primaryKey"
      :fields="fields"
      sort-icon-left
      no-sort-reset
      no-local-sorting
      :per-page="perPage"
      :current-page="currentPage"
      v-bind="$attrs"
      v-on="$listeners"
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
          <b-pagination
            v-model="currentPage"
            :per-page="perPage"
            :total-rows="totalItems"
            last-number
            first-number
            align="center"
          >
            <template #prev-text><b-icon-chevron-left /></template>
            <template #next-text><b-icon-chevron-right /></template>
            <template #ellipsis-text><b-icon-three-dots /></template>
          </b-pagination>
        </b-col>
        <b-col md="auto" col>
          <b-dropdown-select
            header="Choose Page Size"
            placeholder="Page Size"
            v-model="perPage"
            :options="perPageOptions"
            class="paginated-table-dropdown"
          />
        </b-col>
        <b-col cols="auto">
          <b-button class="paginated-table-refresh" @click="refresh">
            <b-icon-arrow-clockwise shift-h="-2" rotate="45" />
          </b-button>
        </b-col>
        <b-col v-for="control in controls" :key="control" md="auto" cols="12" class="ml-md-3">
          <slot :name="`controls(${control})`" />
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
    id: {
      type: String,
      required: true
    },
    title: String,
    fields: {
      type: Array,
      default() {
        return []
      }
    },
    controls: {
      type: Array,
      default() {
        return []
      },
      validator(value) {
        return value.every((control) => typeof control === "string")
      }
    },
    primaryKey: String,
    totalItems: {
      type: Number,
      required: true,
      validator(value) {
        return value >= 0
      }
    },
    provider: {
      type: Function,
      required: true
    },
    refreshRate: {
      type: Number,
      default: -1
    }
  },
  methods: {
    refresh() {
      this.$root.$emit("bv::refresh::table", this.id)
    }
  },
  beforeMount() {
    if (this.refreshRate >= 0) {
      this.intervalId = setInterval(this.refresh, this.refreshRate)
    }
  },
  beforeDestroy() {
    this.intervalId = clearInterval(this.intervalId)
  },
  data() {
    return {
      intervalId: undefined,
      currentPage: 1,
      perPage: 20,
      perPageOptions: [10, 20, 50, 100]
    }
  }
}
</script>
