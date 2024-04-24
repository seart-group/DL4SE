<template>
  <div class="config-table-container">
    <b-overlay :show="busy" variant="light">
      <b-table-simple
        responsive
        borderless
        :hover="hasMappings"
        sticky-header="370px"
        class="config-table-border"
        table-class="config-table"
      >
        <b-thead head-variant="dark" class="config-table-header-row">
          <b-tr>
            <b-th>Property</b-th>
            <b-th>Setting</b-th>
          </b-tr>
        </b-thead>
        <b-tbody class="config-table-body">
          <template v-if="hasMappings">
            <b-tr v-for="key in Object.keys(mappings)" :key="key" class="config-table-row">
              <b-td class="text-monospace">
                <label :for="`${key}_input`" class="config-table-label">
                  {{ key }}
                </label>
              </b-td>
              <b-td>
                <b-input
                  :id="`${key}_input`"
                  :state="configState(key)"
                  :disabled="busy"
                  v-model.trim="mappings[key]"
                  class="config-table-input"
                />
              </b-td>
            </b-tr>
          </template>
          <template v-else>
            <b-tr class="b-table-empty-row config-table-row">
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
      <b-row no-gutters align-h="center">
        <b-col cols="auto">
          <b-button-group>
            <b-button @click="consume" :disabled="consumeDisabled" class="config-table-btn">
              <b-icon-cloud-upload /> Synchronize
            </b-button>
            <b-button @click="refresh" :disabled="busy" class="config-table-btn">
              <b-icon-arrow-clockwise shift-h="-2" rotate="45" />
            </b-button>
          </b-button-group>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import useVuelidate from "@vuelidate/core";
import { required } from "@vuelidate/validators";

export default {
  name: "b-config-table",
  props: {
    id: String,
    supplier: {
      type: Function,
      default() {
        return Promise.reject();
      },
    },
    consumer: {
      type: Function,
      default() {
        return Promise.reject();
      },
    },
  },
  computed: {
    hasMappings() {
      return !!this.mappings && Object.keys(this.mappings).length > 0;
    },
    consumeDisabled() {
      return this.v$.$invalid || !this.v$.$anyDirty || this.busy;
    },
  },
  methods: {
    configDirty(key) {
      return this.v$.mappings[key].$dirty;
    },
    configValid(key) {
      return !this.v$.mappings[key].$invalid;
    },
    configState(key) {
      return this.configDirty(key) ? this.configValid(key) : null;
    },
    async consume() {
      this.busy = true;
      await this.consumer(this.mappings)
        .then((res) => (this.mappings = res))
        .catch(() => (this.mappings = {}));
      // TODO 20.10.22: Add some form of error handling in rare event of failures
      this.v$.$reset();
      this.busy = false;
    },
    async refresh() {
      this.busy = true;
      await this.supplier()
        .then((res) => (this.mappings = res))
        .catch(() => (this.mappings = {}));
      this.v$.$reset();
      this.busy = false;
    },
  },
  async mounted() {
    await this.refresh();
  },
  setup(props) {
    const globalConfig = props.id !== undefined ? { $registerAs: props.id } : {};
    return {
      v$: useVuelidate(globalConfig),
    };
  },
  data() {
    return {
      busy: undefined,
      mappings: undefined,
    };
  },
  validations() {
    if (this.hasMappings) {
      const rules = { $autoDirty: true, required };
      return {
        mappings: Object.keys(this.mappings).reduce((acc, key) => Object.assign(acc, { [key]: rules }), {}),
      };
    } else {
      return {};
    }
  },
};
</script>
