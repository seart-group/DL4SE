<template>
  <b-modal
    :id="id"
    :title="title"
    content-class="details-modal-content"
    footer-class="details-modal-footer"
    scrollable
    centered
    @hidden="reset"
  >
    <b-card no-body class="details-modal-card">
      <b-tabs v-model="activeTab" card @activate-tab="hideTooltip">
        <b-tab
          v-for="{ name, value } in formatted"
          :title="name"
          :key="name.toLowerCase()"
          :disabled="!value"
          title-link-class="details-modal-tab-title"
        >
          <b-card-body>
            <pre class="m-0">{{ value }}</pre>
          </b-card-body>
        </b-tab>
      </b-tabs>
    </b-card>
    <template #modal-footer>
      <b-button :id="`${id}-btn`" class="btn-secondary-border-2" @click="copy">
        <b-icon-clipboard />
      </b-button>
      <b-tooltip
        title="Copied!"
        triggers="click"
        :target="`${id}-btn`"
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
        ]
      },
    },
  },
  computed: {
    formatted() {
      return this.formatters.map((item) => {
        return {
          name: item.name,
          value: item.formatter(this.content),
        }
      })
    },
  },
  methods: {
    reset() {
      this.activeTab = 0
      this.showTooltip = false
      this.$emit("reset")
    },
    copy() {
      const idx = this.activeTab
      const value = this.formatted[idx].value
      navigator.clipboard.writeText(value).then(() => (this.showTooltip = true))
    },
    hideTooltip() {
      this.showTooltip = false
    },
    autoHideTooltip() {
      setTimeout(this.hideTooltip, 2000)
    },
  },
  data() {
    return {
      activeTab: 0,
      showTooltip: false,
    }
  },
}
</script>
