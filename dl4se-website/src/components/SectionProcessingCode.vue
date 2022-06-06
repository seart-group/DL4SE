<template>
  <b-container>
    <b-row>
      <b-col>
        <h5 class="task-form-section-title">Instance Processing</h5>
      </b-col>
    </b-row>
    <b-row align-h="between">
      <b-col xl="7" lg="7" md="6" sm="6" cols="12">
        <b-row no-gutters>
          <b-col xl="3" lg="4" md="12" sm="12" cols="12">
            <b-form-group label="Remove:" label-class="font-weight-bold">
              <b-checkbox id="docstring-checkbox" v-model="local.remove.docstring" inline>
                Docstrings
              </b-checkbox>
              <b-checkbox id="comments-checkbox" v-model="local.remove.inner_comments" inline>
                Inner comments
              </b-checkbox>
            </b-form-group>
          </b-col>
          <b-col xl="6" lg="7" md="12" sm="12" cols="12" offset-lg="1" offset-xl="2">
            <b-form-group label="Mask:" label-class="font-weight-bold">
              <b-masking id="token-mask" v-model="local.masking" />
              <b-checkbox v-model="local.masking.contiguous_only">Only mask contiguous tokens</b-checkbox>
            </b-form-group>
          </b-col>
        </b-row>
      </b-col>
      <b-col xl="5" lg="5" md="6" sm="6" cols="12">
        <b-form-group label="Abstract:" label-class="font-weight-bold">
          <p class="m-0">Abstract source code using the following idioms:</p>
          <b-tag-select id="idioms-tag-select" v-model="local.idioms" placeholder="Idiom..." />
        </b-form-group>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import useVuelidate from "@vuelidate/core";
import BMasking from "@/components/Masking"
import BTagSelect from "@/components/TagSelect"

export default {
  name: "b-section-processing-code",
  components: { BMasking, BTagSelect },
  props: {
    remove: Object,
    masking: Object,
    idioms: Array[String]
  },
  watch: {
    "local.remove": {
      deep: true,
      handler() {
        this.$emit("update:remove", this.local.remove)
      }
    },
    "local.masking": {
      deep: true,
      handler() {
        this.$emit("update:masking", this.local.masking)
      }
    },
    "local.idioms": {
      handler() {
        this.$emit("update:idioms", this.local.idioms)
      }
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
        remove: this.remove,
        masking: this.masking,
        idioms: this.idioms
      }
    }
  }
}
</script>