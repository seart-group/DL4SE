<template>
  <div id="download" v-if="show">
    <h1 class="page-title">
      Download will commence shortly
    </h1>
    <h3 class="page-description text-center">
      Do not close this window, or navigate to other pages until the download is finished.
    </h3>
  </div>
</template>

<script>
import streamSaver from "streamsaver"
import {WritableStream} from "web-streams-polyfill/ponyfill"
import routerMixin from "@/mixins/routerMixin"
// Override the MITM file hosted on GitHub that streamSaver
// uses with the locally stored one
// https://github.com/jimmywarting/StreamSaver.js/issues/242
streamSaver.mitm = "/mitm.html"

export default {
  props: {
    uuid: String
  },
  mixins: [ routerMixin ],
  // Axios can not consume streams on the client-side
  // https://stackoverflow.com/a/58696592/17173324
  async created() {
    const url = "https://localhost:8080/api/task/download/" + this.uuid
    fetch(url, {
      headers: {
        'Content-Type': 'application/gzip',
        'Authorization': this.$store.getters.getToken
      }
    }).then(response => {
      if (!response.ok) throw new Error(response.status)

      this.show = true
      const disposition = response.headers.get('Content-Disposition')
      const length = response.headers.get('Content-Length')
      const fileName = disposition.substring(
          disposition.indexOf('"') + 1,
          disposition.lastIndexOf('"')
      )

      // For Firefox and Safari
      if (!window.WritableStream) {
        streamSaver.WritableStream = WritableStream
        window.WritableStream = WritableStream
      }

      const fileStream = streamSaver.createWriteStream(fileName, { size: length })
      const readableStream = response.body;
      if (readableStream.pipeTo) {
        return readableStream.pipeTo(fileStream)
      }

      window.writer = fileStream.getWriter();

      const reader = response.body.getReader();
      const pump = () => reader.read().then((res) => {
        (res.done) ? window.writer.close() : window.writer.write(res.value).then(pump)
      })

      pump();
    }).catch((err) => {
      if (!err) return
      const status = err.message
      const responseHandlers = {
        400: () => this.redirectDashboardAndToast(
            "Invalid UUID",
            "The specified task UUID is not valid. Make sure you copied the link correctly, and try again.",
            "warning"
        ),
        401: () => {
          this.$store.commit("clearToken")
          this.$router.push({ name: 'login', params: { showLoggedOut: true } })
        },
        403: () => this.redirectDashboardAndToast(
            "Task Download Refused",
            "This task can not be downloaded.",
            "warning"
        ),
        404: () => this.redirectDashboardAndToast(
            "Task Not Found",
            "The specified task could not be found.",
            "warning"
        ),
        410: () => this.redirectDashboardAndToast(
            "Task Download Expired",
            "The download link for this task is no longer valid.",
            "secondary"
        )
      }
      const fallbackHandler = () => this.$router.push({ name: 'home', params: { showServerError: true } })
      const handler = responseHandlers[status] || fallbackHandler
      handler()
    })
  },
  data() {
    return {
      show: false
    }
  }
}
</script>
