<template>
  <b-container>
    <b-row>
      <b-col>
        <h5 class="task-form-section-title">Characteristics of repositories used to build the dataset</h5>
      </b-col>
    </b-row>
    <b-row class="justify-content-between">
      <b-col xl="3" lg="3" md="3" sm="6">
        <div class="p-2 text-center">
          <b-dropdown-select id="language-select" name="language"
                             header="Select a language" not-selected="Language"
                             v-model="dropdown.language"
                             :options="dropdown.options"
          />
        </div>
        <div class="p-2">
          <b-checkbox id="license-checkbox" v-model="checked.has_license">
            Has Open-source License
          </b-checkbox>
          <b-checkbox id="forks-checkbox" v-model="checked.exclude_forks">
            Exclude Forks
          </b-checkbox>
        </div>
      </b-col>
      <b-col xl="4" lg="5" md="6" sm="6">
        <b-range id="commits-range" ref="range-1" field="commits" lower-bound :min="0"
                 v-model:lower.number="count.commits"
                 @update:lower="count.commits = $event"
                 class="p-2"
        />
        <b-range id="contributors-range" ref="range-2" field="contributors" lower-bound :min="0"
                 v-model:lower.number="count.contributors"
                 @update:lower="count.contributors = $event"
                 class="p-2"
        />
      </b-col>
      <b-col xl="4" lg="4" md="6" sm="6">
        <b-range id="issues-range" ref="range-3" field="issues" lower-bound :min="0"
                 v-model:lower.number="count.issues"
                 @update:lower="count.issues = $event"
                 class="p-2"
        />
        <b-range id="stars-range" ref="range-4" field="stars" lower-bound :min="0"
                 v-model:lower.number="count.stars"
                 @update:lower="count.stars = $event"
                 class="p-2"
        />
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import BDropdownSelect from "@/components/DropdownSelect"
import BRange from "@/components/Range"

export default {
  name: "b-repo-section",
  components: { BDropdownSelect, BRange },
  props: {
    language: String,
    options: Array[String],
    has_license: Boolean,
    exclude_forks: Boolean,
    commits: Number,
    contributors: Number,
    issues: Number,
    stars: Number
  },
  watch: {
    "dropdown.language": function () {
      this.$emit("update:language", this.dropdown.language)
    },
    "checked.has_license": function () {
      this.$emit("update:has-license", this.checked.has_license)
    },
    "checked.exclude_forks": function () {
      this.$emit("update:exclude-forks", this.checked.exclude_forks)
    },
    "count.commits" : function () {
      this.$emit("update:commits", this.count.commits)
    },
    "count.contributors" : function () {
      this.$emit("update:contributors", this.count.contributors)
    },
    "count.issues" : function () {
      this.$emit("update:issues", this.count.issues)
    },
    "count.stars" : function () {
      this.$emit("update:stars", this.count.stars)
    }
  },
  computed: {
    state() {
      return !!this.dropdown.language &&
          Object.values(this.$refs).map(ref => ref.state)
              .filter(x => x !== null)
              .reduce((acc, curr) => acc && curr, true)
    }
  },
  data() {
    return {
      dropdown: {
        language: this.language,
        options: this.options
      },
      checked: {
        has_license: this.has_license,
        exclude_forks: this.exclude_forks
      },
      count: {
        commits: this.commits,
        contributors: this.contributors,
        issues: this.issues,
        stars: this.stars
      }
    }
  }
}
</script>