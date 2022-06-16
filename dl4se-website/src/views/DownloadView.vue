<template>
  <div id="download">
    <h5 class="page-title">
      Dataset download will commence shortly. <br />
      Do not close this window, or navigate to other pages until the download finishes.
    </h5>
  </div>
</template>

<script>
import streamSaver from 'streamsaver'
import {WritableStream} from 'web-streams-polyfill/ponyfill';
// Override the MITM file hosted on GitHub that streamSaver
// uses with the locally stored one
// https://github.com/jimmywarting/StreamSaver.js/issues/242
streamSaver.mitm = "/mitm.html"

export default {
  props: {
    uuid: String
  },
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
    }).catch(() => {})
    // TODO 16.06.22: What kind of handling should we do here?
  }
}
</script>
