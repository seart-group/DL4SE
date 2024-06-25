<template>
  <b-modal :id="id" :title="title" scrollable centered @hidden="reset">
    <b-card no-body>
      <b-tabs v-model="activeTab" :align="tabsAlign" card @activate-tab="hideTooltip">
        <b-tab v-for="{ name, value } in formatted" :key="name.toLowerCase()" :title="name" :disabled="!value">
          <b-highlighted-code :language="name.toLowerCase()" :code="value" />
        </b-tab>
      </b-tabs>
    </b-card>
    <template #modal-footer>
      <b-button :id="tooltipId" :block="footerButtonBlock" @click="copy">
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
import BHighlightedCode from "@/components/HighlightedCode";

export default {
  name: "b-details-modal",
  components: { BHighlightedCode },
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
    tabsAlign: {
      type: String,
      default: null,
    },
    footerButtonBlock: {
      type: Boolean,
      default: false,
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
