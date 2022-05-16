<template>
  <b-dropdown :style="{ minWidth: dMinWidth + 'em', maxWidth: '100%' }" no-caret>
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
  },
  computed: {
    dMinWidth() {
      return Math.max.apply(0, this.options.map(({ length }) => length))
    }
  }
}
</script>