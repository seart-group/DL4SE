<template>
  <div id="task">
    <h1 class="page-title">Specify your dataset</h1>
    <b-section-repo class="task-form-section-top"
                    :options="options.languages"
                    :language="task.query.language_name"
                    @update:language="task.query.language_name = $event"
                    :has-license="task.query.has_license"
                    @update:has-license="task.query.has_license = $event"
                    :commits="{ lower: task.query.min_commits }"
                    @update:commits="task.query.min_commits = $event.lower"
                    :contributors="{ lower: task.query.min_contributors }"
                    @update:contributors="task.query.min_contributors = $event.lower"
                    :issues="{ lower: task.query.min_issues }"
                    @update:issues="task.query.min_issues = $event.lower"
                    :stars="{ lower: task.query.min_stars }"
                    @update:stars="task.query.min_stars = $event.lower"
                    :exclude="{ forks: task.query.exclude_forks }"
                    @update:exclude="task.query.exclude_forks = $event.forks"
    />
    <b-container class="py-4 task-form-section-middle">
      <b-row>
        <b-col>
          <h5 class="task-form-section-title">Dataset Characteristics</h5>
        </b-col>
      </b-row>
      <b-row>
        <b-col>
          <b-form-group label="Each instance is a:" label-class="font-weight-bold" class="m-0">
            <b-form-radio-group id="type-radio" required v-model="task.query.type" :options="options.types" />
          </b-form-group>
        </b-col>
      </b-row>
    </b-container>
    <b-section-filters-code class="pb-4 task-form-section-middle" :granularity="task.query.type"
                            :characters="{ lower: task.query.min_characters, upper: task.query.max_characters }"
                            @update:characters="task.query.min_characters = $event.lower
                                                task.query.max_characters = $event.upper"
                            :tokens="{ lower: task.query.min_tokens, upper: task.query.max_tokens }"
                            @update:tokens="task.query.min_tokens = $event.lower
                                            task.query.max_tokens = $event.upper"
                            :lines="{ lower: task.query.min_lines, upper: task.query.max_lines }"
                            @update:lines="task.query.min_lines = $event.lower
                                           task.query.max_lines = $event.upper"
                            :exclude="{
                              test: task.query.exclude_test,
                              boilerplate: task.query.exclude_boilerplate,
                              unparsable: task.query.exclude_unparsable,
                              nonAscii: task.query.exclude_non_ascii,
                              duplicates: task.query.exclude_duplicates,
                              identical: task.query.exclude_identical
                            }" @update:exclude="task.query.exclude_test = $event.test
                                                task.query.exclude_boilerplate = $event.boilerplate
                                                task.query.exclude_unparsable = $event.unparsable
                                                task.query.exclude_non_ascii = $event.nonAscii
                                                task.query.exclude_duplicates = $event.duplicates
                                                task.query.exclude_identical = $event.identical"
    />
    <b-section-processing-code class="task-form-section-middle"
                               :remove="{
                                 docstring: task.processing.remove_docstring,
                                 innerComments: task.processing.remove_inner_comments
                               }" @update:remove="task.processing.remove_docstring = $event.docstring
                                                  task.processing.remove_inner_comments = $event.innerComments"
                               :masking="{
                                 token: task.processing.mask_token,
                                 percentage: task.processing.mask_percentage,
                                 contiguousOnly: task.processing.mask_contiguous_only
                               }" @update:masking="task.processing.mask_token = $event.token
                                                   task.processing.mask_percentage = $event.percentage
                                                   task.processing.mask_contiguous_only = $event.contiguousOnly"
                               :idioms="task.processing.idioms" @update:idioms="task.processing.idioms = $event"
    />
    <b-container class="py-4 task-form-section-bottom">
      <b-row align-h="center">
        <b-col cols="auto">
          <b-button :disabled="submitDisabled" @click="submit" class="action-btn">Generate Dataset</b-button>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import useVuelidate from "@vuelidate/core";
import BSectionRepo from "@/components/SectionRepo"
import BSectionFiltersCode from "@/components/SectionFiltersCode"
import BSectionProcessingCode from "@/components/SectionProcessingCode"
import axios from "axios";

export default {
  components: {
    BSectionRepo,
    BSectionFiltersCode,
    BSectionProcessingCode
  },
  computed: {
    submitDisabled() {
      return this.v$.$invalid
    }
  },
  methods: {
    async submit() {
      const payload = this.task
      const config = { headers : { 'authorization': this.$store.getters.getToken } }
      await axios.post("https://localhost:8080/api/task/create", payload, config)
    }
  },
  setup() {
    return {
      v$: useVuelidate()
    }
  },
  data() {
    return {
      task: {
        query: {
          type : "file",
          language_name: null,
          has_license : false,
          min_commits: null,
          min_contributors: null,
          min_issues: null,
          min_stars: null,
          min_tokens: null,
          max_tokens: null,
          min_lines: null,
          max_lines: null,
          min_characters: null,
          max_characters: null,
          include_ast: true,
          exclude_forks: false,
          exclude_duplicates: false,
          exclude_identical: false,
          exclude_test: false,
          exclude_non_ascii: false,
          exclude_unparsable: false,
          exclude_boilerplate: false
        },
        processing: {
          remove_docstring: false,
          remove_inner_comments: false,
          mask_token: null,
          mask_percentage: null,
          mask_contiguous_only: null,
          idioms: []
        }
      },
      options: {
        types: [
          {
            text: "File",
            value: "file"
          },
          {
            text: "Function",
            value: "function"
          }
        ],
        languages: ['Java', 'Python', 'C++'],
      }
    }
  }
}
</script>