<template>
  <b-container fluid class="footer-container">
    <b-row align-h="between">
      <b-col md="auto" sm="12" v-if="showAuthors">
        <div class="footer-text">Created by:&nbsp;</div>
        <template v-for="(author, idx) in authors">
          <b-link :href="author.url" target="_blank" class="footer-link" :key="`author-${idx}`">
            {{ author.name }}
          </b-link>
          <div v-if="(idx + 1) < authors.length"
               class="footer-link-separator"
               :key="`separator-${idx}`"
          >,&nbsp;</div>
        </template>
      </b-col>
      <b-col md="auto" sm="12" v-if="showOrganisation">
        <div class="footer-text">Maintained by:&nbsp;</div>
        <b-link :href="organisation.url" target="_blank" class="footer-link">
          {{ organisation.name }}
        </b-link>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
export default {
  name: "b-footer",
  props: {
    authors: {
      type: Array,
      default() {
        return [
          {
            name: "Giulio Ferrari",
            url: "https://example.com"
          }
        ]
      }
    },
    organisation: {
      type: Object,
      default() {
        return {
          name: "The Example Organization",
          url: "https://example.com"
        }
      }
    }
  },
  computed: {
    showAuthors() {
      return !!this.authors?.length
    },
    showOrganisation() {
      return !!this.organisation
    }
  }
}
</script>