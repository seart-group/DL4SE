<template>
  <div class="dialog-page">
    <h1 class="dialog-page-title">{{ title }}</h1>
    <h3 class="dialog-page-description" v-if="description">
      {{ description }}
    </h3>
    <b-container class="dialog-page-container">
      <b-row align-h="center">
        <b-col
          xl="2"
          lg="2"
          md="3"
          sm="12"
          cols="12"
          class="dialog-page-column"
          v-for="({ text, icon, action }, idx) in actions"
          :key="idx"
        >
          <b-button
            class="btn-secondary-border-2"
            :disabled="disabled"
            @click="
              disabled = true
              action()
            "
          >
            <b-icon :icon="icon" />
            {{ text }}
          </b-button>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
export default {
  name: "b-dialog-page",
  props: {
    title: {
      type: String,
      required: true
    },
    description: {
      type: String,
      required: false
    },
    actions: {
      type: Array,
      default() {
        return []
      }
    }
  },
  data() {
    return {
      disabled: false
    }
  }
}
</script>
