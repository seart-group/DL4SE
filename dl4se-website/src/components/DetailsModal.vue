<template>
  <b-modal :id="id" :title="title" scrollable centered @hidden="reset">
    <b-card no-body>
      <b-tabs v-model="activeTab" card @activate-tab="hideTooltip">
        <b-tab v-for="{ name, value } in formatted" :key="name.toLowerCase()" :title="name" :disabled="!value">
          <b-card-body>
            <pre><code class="text-monospace">{{ value }}</code></pre>
          </b-card-body>
        </b-tab>
      </b-tabs>
    </b-card>
    <template #modal-footer>
      <b-button :id="tooltipId" @click="copy">
        <b-icon-clipboard />
      </b-button>
      <b-tooltip
        title="Copied!"
        triggers="click"
        :target="tooltipId"
        :show.sync="showTooltip"
        @shown="autoHideTooltip"
      />
    </template>
  </b-modal>
</template>

<script>
export default {
  name: "b-details-modal",
  props: {
    id: {
      type: String,
      required: true,
    },
    title: {
      type: String,
      required: true,
    },
    content: {
      type: [String, Object],
      required: true,
    },
    formatters: {
      type: Array,
      default() {
        return [
          {
            name: "Identity",
            formatter: String,
          },
        ];
      },
    },
  },
  computed: {
    formatted() {
      return this.formatters.map((item) => {
        return {
          name: item.name,
          value: item.formatter(this.content),
        };
      });
    },
    tooltipId() {
      return `${this.id}___BV_modal_footer_tooltip_`;
    },
  },
  methods: {
    reset() {
      this.activeTab = 0;
      this.showTooltip = false;
      this.$emit("reset");
    },
    copy() {
      const idx = this.activeTab;
      const value = this.formatted[idx].value;
      navigator.clipboard.writeText(value).then(() => (this.showTooltip = true));
    },
    hideTooltip() {
      this.showTooltip = false;
    },
    autoHideTooltip() {
      setTimeout(this.hideTooltip, 2000);
    },
  },
  data() {
    return {
      activeTab: 0,
      showTooltip: false,
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/details-modal.sass" />
