<template>
  <div>
    <b-overlay :show="busy" variant="light">
      <b-table-simple
        class="border-secondary border-bottom"
        sticky-header="370px"
        :hover="hasMappings"
        responsive
        borderless
      >
        <b-thead head-variant="dark">
          <b-tr>
            <b-th>Property</b-th>
            <b-th>Setting</b-th>
          </b-tr>
        </b-thead>
        <b-tbody class="bg-light">
          <template v-if="hasMappings">
            <b-tr v-for="key in Object.keys(mappings)" :key="key">
              <b-td class="text-monospace">
                <label :for="`${key}_input`" class="m-0">
                  {{ key }}
                </label>
              </b-td>
              <b-td>
                <b-input
                  :id="`${key}_input`"
                  :state="configState(key)"
                  :disabled="busy"
                  v-model.trim="mappings[key]"
                  class="border-secondary border-top-0 border-left-0 border-right-0 rounded-0"
                />
              </b-td>
            </b-tr>
          </template>
          <template v-else>
            <b-tr class="b-table-empty-row bg-light">
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
            <b-button @click="consume" :disabled="consumeDisabled">
              <b-icon-cloud-upload /> Synchronize
            </b-button>
            <b-button @click="refresh" :disabled="busy">
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

<style scoped lang="sass" src="@/assets/styles/component/config-table.sass" />
