<template>
  <b-navbar id="smart-navbar" class="smart-navbar" sticky toggleable="sm">
    <b-navbar-brand>DL4SE</b-navbar-brand>
    <b-navbar-toggle target="smart-navbar-collapse" />
    <b-collapse is-nav id="smart-navbar-collapse">
      <b-navbar-nav>
        <slot name="nav-items" />
      </b-navbar-nav>
      <b-navbar-nav v-if="dropdownShow" class="ml-auto">
        <b-nav-item-dropdown right>
          <template #button-content>
            <b-icon-person-fill />
          </template>
          <slot name="dropdown-items" />
        </b-nav-item-dropdown>
      </b-navbar-nav>
    </b-collapse>
  </b-navbar>
</template>

<script>
export default {
  name: "b-smart-navbar",
  props: {
    brand: {
      type: String,
      default: "App"
    },
    dropdownShow: {
      type: Boolean,
      default: true
    }
  }
}
</script>