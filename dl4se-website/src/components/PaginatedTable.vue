<template>
  <div class="table-paginated">
    <b-table
      :id="id"
      :items="provider"
      :fields="fields"
      :primary-key="primaryKey"
      :per-page="perPage"
      :current-page="currentPage"
      v-bind="$attrs"
      v-on="$listeners"
      class="table-container"
      head-variant="dark"
      sort-icon-left
      no-sort-reset
      no-local-sorting
      show-empty
      borderless
      responsive
      hover
    >
      <template v-for="(_, scopedSlotName) in $scopedSlots" v-slot:[scopedSlotName]="slotData">
        <slot :name="scopedSlotName" v-bind="slotData" />
      </template>
      <template v-for="(_, slotName) in $slots" v-slot:[slotName]>
        <slot :name="slotName" />
      </template>
    </b-table>
    <b-container tag="nav" class="controls-container">
      <b-row no-gutters align-h="center">
        <b-col cols="12" md="auto">
          <b-pagination
            v-model="currentPage"
            :per-page="perPage"
            :total-rows="totalItems"
            align="center"
            first-number
            last-number
          >
            <template #prev-text><b-icon-chevron-left /></template>
            <template #next-text><b-icon-chevron-right /></template>
            <template #ellipsis-text><b-icon-three-dots /></template>
          </b-pagination>
        </b-col>
        <b-col cols="12" md="auto">
          <b-input-group>
            <b-dropdown-select v-model="perPage" :options="perPageOptions" class="flex-grow-1">
              <template #header>Choose page size</template>
            </b-dropdown-select>
            <b-button @click="refresh" class="btn-controls ratio-1x1">
              <b-icon-arrow-clockwise shift-h="-2" rotate="45" />
            </b-button>
          </b-input-group>
        </b-col>
        <b-col v-for="control in controls" :key="control" cols="12" md="auto">
          <slot :name="`controls(${control})`" />
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import BDropdownSelect from "@/components/DropdownSelect";

export default {
  name: "b-paginated-table",
  components: { BDropdownSelect },
  props: {
    id: {
      type: String,
      required: true,
    },
    fields: {
      type: Array,
      default() {
        return [];
      },
    },
    controls: {
      type: Array,
      default() {
        return [];
      },
      validator(value) {
        return value.every((control) => typeof control === "string");
      },
    },
    primaryKey: String,
    totalItems: {
      type: Number,
      required: true,
      validator(value) {
        return value >= 0;
      },
    },
    provider: {
      type: Function,
      required: true,
    },
    refreshRate: {
      type: Number,
      default: -1,
    },
  },
  methods: {
    refresh() {
      this.$root.$emit("bv::refresh::table", this.id);
    },
  },
  beforeMount() {
    if (this.refreshRate >= 0) {
      this.intervalId = setInterval(this.refresh, this.refreshRate);
    }
  },
  beforeDestroy() {
    this.intervalId = clearInterval(this.intervalId);
  },
  data() {
    return {
      intervalId: undefined,
      currentPage: 1,
      perPage: 20,
      perPageOptions: [10, 20, 50, 100],
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/paginated-table.sass" />
