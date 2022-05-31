<template>
  <b-container>
    <b-row>
      <b-col>
        <h5 class="task-form-section-title">Instance Filters</h5>
      </b-col>
    </b-row>
    <b-row align-h="center">
      <b-col lg="3" md="6" sm="12" cols="12">
        <b-form-group label="Exclude:" label-class="font-weight-bold">
          <b-checkbox id="test-code-checkbox" v-model="checked.test">
            Test code
          </b-checkbox>
          <b-checkbox id="boilerplate-code-checkbox"
                      v-if="granularity === 'function'"
                      v-model="checked.boilerplate"
          >
            Boilerplate code
          </b-checkbox>
          <b-checkbox id="unparsable-code-checkbox"
                      v-if="granularity === 'file'"
                      v-model="checked.unparsable"
          >
            Unparsable code
          </b-checkbox>
          <b-checkbox id="non-ascii-checkbox" v-model="checked.non_ascii">
            Instances with non-ASCII characters
          </b-checkbox>
        </b-form-group>
      </b-col>
      <b-col lg="2" md="6" sm="12" cols="12">
        <b-form-group label="Ignore:" label-class="font-weight-bold">
          <b-checkbox id="duplicates-checkbox" inline
                      v-model="checked.duplicates"
                      @change="() => { if (checked.identical) checked.identical = false }"
          >
            Duplicates
          </b-checkbox>
          <b-checkbox id="clones-checkbox" inline
                      v-model="checked.identical"
                      @change="() => { if (checked.duplicates) checked.duplicates = false }"
          >
            Near-clones
          </b-checkbox>
        </b-form-group>
      </b-col>
      <b-col lg md="9" sm="12" cols="12">
        <b-range id="characters-range" field="characters" :min="0"
                 lower-bound v-model:lower.number="count.characters.min" @update:lower="count.characters.min = $event"
                 upper-bound v-model:upper.number="count.characters.max" @update:upper="count.characters.max = $event"
        />
        <b-range id="tokens-range" field="tokens" :min="0"
                 lower-bound v-model:lower.number="count.tokens.min" @update:lower="count.tokens.min = $event"
                 upper-bound v-model:upper.number="count.tokens.max" @update:upper="count.tokens.max = $event"
        />
        <b-range id="lines-range" field="lines" :min="0"
                 lower-bound v-model:lower.number="count.lines.min" @update:lower="count.lines.min = $event"
                 upper-bound v-model:upper.number="count.lines.max" @update:upper="count.lines.max = $event"
        />
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import BRange from "@/components/Range"

export default {
  name: "b-code-filters-section",
  components: { BRange },
  props: {
    granularity: {
      type: String,
      required: true,
      validator(value) {
        return ["file", "function"].includes(value)
      }
    },
    exclude: Object,
    characters: Object,
    tokens: Object,
    lines: Object
  },
  watch: {
    "checked.test": function () {
      this.$emit("update:test", this.checked.test)
    },
    "checked.boilerplate": function () {
      this.$emit("update:boilerplate", this.checked.boilerplate)
    },
    "checked.unparsable": function () {
      this.$emit("update:unparsable", this.checked.unparsable)
    },
    "checked.non_ascii": function () {
      this.$emit("update:non-ascii", this.checked.non_ascii)
    },
    "checked.identical": function () {
      this.$emit("update:identical", this.checked.identical)
    },
    "checked.duplicates": function () {
      this.$emit("update:duplicates", this.checked.duplicates)
    },
    "count.characters.min": function () {
      this.$emit("update:characters:min", this.count.characters.min)
    },
    "count.characters.max": function () {
      this.$emit("update:characters:max", this.count.characters.max)
    },
    "count.tokens.min": function () {
      this.$emit("update:tokens:min", this.count.tokens.min)
    },
    "count.tokens.max": function () {
      this.$emit("update:tokens:max", this.count.tokens.max)
    },
    "count.lines.min": function () {
      this.$emit("update:lines:min", this.count.lines.min)
    },
    "count.lines.max": function () {
      this.$emit("update:lines:max", this.count.lines.max)
    }
  },
  data() {
    return {
      checked: {
        test: this.exclude.test,
        boilerplate: this.exclude.boilerplate,
        unparsable: this.exclude.unparsable,
        non_ascii: this.exclude.non_ascii,
        identical: this.exclude.identical,
        duplicates: this.exclude.duplicates
      },
      count: {
        characters: {
          min: this.characters.min,
          max: this.characters.max
        },
        tokens: {
          min: this.tokens.min,
          max: this.tokens.max
        },
        lines: {
          min: this.lines.min,
          max: this.lines.max
        }
      }
    }
  }
}
</script>