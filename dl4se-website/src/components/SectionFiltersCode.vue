<template>
  <b-container>
    <b-row>
      <b-col>
        <h5 class="task-form-section-title">Instance Filters</h5>
      </b-col>
    </b-row>
    <b-row align-h="center">
      <b-col lg="3" md="6" sm="12">
        <b-form-group label="Exclude:" label-class="font-weight-bold">
          <b-checkbox id="test-code-checkbox" v-model="local.exclude.test">
            Test code
          </b-checkbox>
          <b-checkbox id="boilerplate-code-checkbox"
                      v-if="granularity === 'function'"
                      v-model="local.exclude.boilerplate"
          >
            Boilerplate code
          </b-checkbox>
          <b-checkbox id="unparsable-code-checkbox"
                      v-if="granularity === 'file'"
                      v-model="local.exclude.unparsable"
          >
            Unparsable code
          </b-checkbox>
          <b-checkbox id="non-ascii-checkbox" v-model="local.exclude.non_ascii">
            Instances with non-ASCII characters
          </b-checkbox>
        </b-form-group>
      </b-col>
      <b-col lg="2" md="6" sm="12">
        <b-form-group label="Ignore:" label-class="font-weight-bold">
          <b-checkbox id="duplicates-checkbox" inline
                      v-model="local.exclude.duplicates"
                      @change="() => { if (local.exclude.identical) local.exclude.identical = false }"
          >
            Duplicates
          </b-checkbox>
          <b-checkbox id="clones-checkbox" inline
                      v-model="local.exclude.identical"
                      @change="() => { if (local.exclude.duplicates) local.exclude.duplicates = false }"
          >
            Near-clones
          </b-checkbox>
        </b-form-group>
      </b-col>
      <b-col lg="7" md="9" sm="12">
        <b-range id="characters-range" field="characters"
                 lower-bound upper-bound :min="0" v-model="local.characters"
        />
        <b-range id="tokens-range" field="tokens"
                 lower-bound upper-bound :min="0" v-model="local.tokens"
        />
        <b-range id="lines-range" field="lines"
                 lower-bound upper-bound :min="0" v-model="local.lines"
        />
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import BRange from "@/components/Range"

export default {
  name: "b-section-filters-code",
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
    "local.exclude": {
      deep: true,
      handler() {
        this.$emit("update:exclude", this.local.exclude)
      }
    },
    "local.characters": {
      deep: true,
      handler() {
        this.$emit("update:characters", this.local.characters)
      }
    },
    "local.tokens": {
      deep: true,
      handler() {
        this.$emit("update:tokens", this.local.tokens)
      }
    },
    "local.lines": {
      deep: true,
      handler() {
        this.$emit("update:lines", this.local.lines)
      }
    }
  },
  data() {
    return {
      local: {
        exclude: this.exclude,
        characters: this.characters,
        tokens: this.tokens,
        lines: this.lines
      }
    }
  }
}
</script>