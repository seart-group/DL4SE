<template>
  <b-container>
    <b-row>
      <b-col>
        <h5 class="task-form-section-title">Repository Sample Characteristics</h5>
      </b-col>
    </b-row>
    <b-row class="justify-content-md-between align-items-xl-center">
      <b-col xl="5" lg="4" md="5" sm="12">
        <b-row no-gutters align-h="between" align-v="center">
          <b-col xl="4" lg="12">
            <b-form-group class="m-0 py-2" :state="dropdownState">
              <b-dropdown-select id="language-select" header="Select a language" placeholder="Language"
                                 v-model="local.language" :options="options" required
              />
              <template #invalid-feedback>
                You must select a language
              </template>
            </b-form-group>
          </b-col>
          <b-col xl="7" lg="12">
            <b-form-group class="m-0 py-2">
              <b-checkbox id="license-checkbox" v-model="local.hasLicense" inline>
                Has Open-source License
              </b-checkbox>
              <b-checkbox id="forks-checkbox" v-model="local.exclude.forks" inline>
                Exclude Forks
              </b-checkbox>
            </b-form-group>
          </b-col>
        </b-row>
      </b-col>
      <b-col xl="7" lg="8" md="6" sm="12">
        <b-row no-gutters class="justify-content-lg-around">
          <b-col lg="6" md="12">
            <b-range id="commits-range" field="commits" lower-bound :min="0" v-model="local.commits" />
          </b-col>
          <b-col lg="6" md="12">
            <b-range id="issues-range" field="issues" lower-bound :min="0" v-model="local.issues" />
          </b-col>
          <b-col lg="6" md="12">
            <b-range id="contributors-range" field="contributors" lower-bound :min="0" v-model="local.contributors" />
          </b-col>
          <b-col lg="6" md="12">
            <b-range id="stars-range" field="stars" lower-bound :min="0" v-model="local.stars" />
          </b-col>
        </b-row>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import useVuelidate from '@vuelidate/core'
import BDropdownSelect from "@/components/DropdownSelect"
import BRange from "@/components/Range"

export default {
  name: "b-section-repo",
  components: { BDropdownSelect, BRange },
  props: {
    language: String,
    options: Array[String],
    hasLicense: Boolean,
    exclude: Object,
    commits: Object,
    contributors: Object,
    issues: Object,
    stars: Object
  },
  watch: {
    "local.language": function () {
      this.$emit("update:language", this.local.language)
    },
    "local.hasLicense": function () {
      this.$emit("update:has-license", this.local.hasLicense)
    },
    "local.exclude": {
      deep: true,
      handler() {
        this.$emit("update:exclude", this.local.exclude)
      }
    },
    "local.commits": {
      deep: true,
      handler() {
        this.$emit("update:commits", this.local.commits)
      }
    },
    "local.contributors": {
      deep: true,
      handler() {
        this.$emit("update:contributors", this.local.contributors)
      }
    },
    "local.issues": {
      deep: true,
      handler() {
        this.$emit("update:issues", this.local.issues)
      }
    },
    "local.stars": {
      deep: true,
      handler() {
        this.$emit("update:stars", this.local.stars)
      }
    }
  },
  computed: {
    dropdownState() {
      const child$ = this.v$.$getResultsForChild("language-select")
      return (child$) ? !child$.$invalid : null
    }
  },
  setup() {
    return {
      v$: useVuelidate()
    }
  },
  data() {
    return {
      local: {
        language: this.language,
        options: this.options,
        hasLicense: this.hasLicense,
        exclude: this.exclude,
        commits: this.commits,
        contributors: this.contributors,
        issues: this.issues,
        stars: this.stars
      }
    }
  }
}
</script>