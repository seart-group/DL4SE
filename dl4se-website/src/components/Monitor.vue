<template>
  <div class="monitor">
    <b-overlay :show="busy" variant="light">
      <b-textarea :value="value" rows="15" wrap="off" readonly class="monitor-textarea" />
    </b-overlay>
    <b-container>
      <b-row no-gutters align-h="center">
        <b-col cols="auto">
          <b-button-group>
            <b-button @click="this.download" :disabled="busy" class="monitor-btn">
              <b-icon-download /> Download
            </b-button>
            <b-button @click="this.refresh" :disabled="busy" class="monitor-btn">
              <b-icon-arrow-clockwise shift-h="-2" rotate="45" />
            </b-button>
          </b-button-group>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
export default {
  name: "b-monitor",
  props: {
    supplier: {
      type: Function,
      default() {
        return Promise.reject()
      }
    }
  },
  methods: {
    download() {
      const link = document.createElement("a")
      link.download = "server.log"
      link.href = URL.createObjectURL(new Blob([this.value]))
      link.click()
    },
    async refresh() {
      this.busy = true
      await this.supplier()
        .then((res) => (this.value = res))
        .catch(() => (this.value = ""))
      this.busy = false
    }
  },
  async mounted() {
    await this.refresh()
  },
  data() {
    return {
      busy: undefined,
      value: undefined
    }
  }
}
</script>
