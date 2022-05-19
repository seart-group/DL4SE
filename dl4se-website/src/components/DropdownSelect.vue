<template>
  <b-dropdown no-caret toggle-class="dropdown-toggle-btn" menu-class="rounded-0">
    <template #button-content>
      {{ selected }} <b-icon-chevron-down />
    </template>
    <b-dropdown-header v-if="header">{{ header }}</b-dropdown-header>
    <b-dropdown-item v-for="option in options"
                     :key="option"
                     :value="option"
                     @click="setValue(option)"
    >
      {{ option }}
    </b-dropdown-item>
  </b-dropdown>
</template>

<script>
export default {
  name: "b-dropdown-select",
  props: {
    header: String,
    selected: String,
    options: Array[String],
    setValue: Function
  }
}
</script>