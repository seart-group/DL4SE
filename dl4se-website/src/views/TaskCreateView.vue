<template>
  <div id="task">
    <h1 class="page-title">Specify your dataset</h1>
    <b-section-repo class="task-form-section-top"
                    :options="options.languages"
                    :language="task.query.language_name" @update:language="task.query.language_name = $event"
                    :has-license="task.query.has_license" @update:has-license="task.query.has_license = $event"
                    :commits="commits" @update:commits="updateCommits"
                    :contributors="contributors" @update:contributors="updateContributors"
                    :issues="issues" @update:issues="updateIssues"
                    :stars="stars" @update:stars="updateStars"
                    :exclude="exclude" @update:exclude="updateExclude"
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
            <b-form-radio-group id="type-radio" required
                                v-model="task.query.granularity"
                                :options="options.granularities"
            />
          </b-form-group>
        </b-col>
      </b-row>
    </b-container>
    <b-section-filters-code class="pb-4 task-form-section-middle" :granularity="task.query.granularity"
                            :characters="characters" @update:characters="updateCharacters"
                            :tokens="tokens" @update:tokens="updateTokens"
                            :lines="lines" @update:lines="updateLines"
                            :exclude="exclude" @update:exclude="updateExclude"
    />
    <b-section-processing-code class="task-form-section-middle"
                               :remove="remove" @update:remove="updateRemove"
                               :masking="masking" @update:masking="updateMasking"
                               :idioms="task.processing.idioms" @update:idioms="updateIdioms"
    />
    <b-container class="py-4 task-form-section-bottom">
      <b-row align-h="center">
        <b-col cols="auto">
          <b-button :disabled="v$.$invalid" @click="submit" class="action-btn">Generate Dataset</b-button>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import axios from "axios";
import axiosMixin from "@/mixins/axiosMixin";
import bootstrapMixin from "@/mixins/bootstrapMixin";
import useVuelidate from "@vuelidate/core";
import BSectionRepo from "@/components/SectionRepo"
import BSectionFiltersCode from "@/components/SectionFiltersCode"
import BSectionProcessingCode from "@/components/SectionProcessingCode"

export default {
  components: {
    BSectionRepo,
    BSectionFiltersCode,
    BSectionProcessingCode
  },
  mixins: [ axiosMixin, bootstrapMixin ],
  props: {
    uuid: String
  },
  computed: {
    commits() {
      return { lower: this.task.query.min_commits }
    },
    contributors() {
      return { lower: this.task.query.min_contributors }
    },
    issues() {
      return { lower: this.task.query.min_issues }
    },
    stars() {
      return { lower: this.task.query.min_stars }
    },
    characters() {
      return {
        lower: this.task.query.min_characters,
        upper: this.task.query.max_characters
      }
    },
    tokens() {
      return {
        lower: this.task.query.min_tokens,
        upper: this.task.query.max_tokens
      }
    },
    lines() {
      return {
        lower: this.task.query.min_lines,
        upper: this.task.query.max_lines
      }
    },
    exclude() {
      return {
        forks: this.task.query.exclude_forks,
        test: this.task.query.exclude_test,
        boilerplate: this.task.query.exclude_boilerplate,
        unparsable: this.task.query.exclude_unparsable,
        nonAscii: this.task.query.exclude_non_ascii,
        duplicates: this.task.query.exclude_duplicates,
        identical: this.task.query.exclude_identical
      }
    },
    remove() {
      return {
        docstring: this.task.processing.remove_docstring,
        innerComments: this.task.processing.remove_inner_comments
      }
    },
    masking() {
      return {
        token: this.task.processing.mask_token,
        percentage: this.task.processing.mask_percentage,
        contiguousOnly: this.task.processing.mask_contiguous_only
      }
    }
  },
  methods: {
    updateCommits(event) {
      this.task.query.min_commits = event.lower
    },
    updateContributors(event) {
      this.task.query.min_contributors = event.lower
    },
    updateIssues(event) {
      this.task.query.min_issues = event.lower
    },
    updateStars(event) {
      this.task.query.min_stars = event.lower
    },
    updateCharacters(event) {
      this.task.query.min_characters = event.lower
      this.task.query.max_characters = event.upper
    },
    updateTokens(event) {
      this.task.query.min_tokens = event.lower
      this.task.query.max_tokens = event.upper
    },
    updateLines(event) {
      this.task.query.min_lines = event.lower
      this.task.query.max_lines = event.upper
    },
    updateExclude(event) {
      this.task.query.exclude_forks = event.forks
      this.task.query.exclude_test = event.test
      this.task.query.exclude_boilerplate = event.boilerplate
      this.task.query.exclude_unparsable = event.unparsable
      this.task.query.exclude_non_ascii = event.nonAscii
      this.task.query.exclude_duplicates = event.duplicates
      this.task.query.exclude_identical = event.identical
    },
    updateRemove(event) {
      this.task.processing.remove_docstring = event.docstring
      this.task.processing.remove_inner_comments = event.innerComments
    },
    updateMasking(event) {
      this.task.processing.mask_token = event.token
      this.task.processing.mask_percentage = event.percentage
      this.task.processing.mask_contiguous_only = event.contiguousOnly
    },
    updateIdioms(event) {
      this.task.processing.idioms = event
    },
    submitSuccess() {
      this.redirectDashboardAndToast(
          "Task Created",
          "Your task has been accepted. Please note that it may take some time until it begins executing.",
          "secondary"
      )
    },
    submitFailure(err) {
      const status = err.response.status
      const handler = this.errorHandlers[status]
      handler()
    },
    async submit() {
      const payload = this.task
      const config = { headers : { 'authorization': this.$store.getters.getToken } }
      const url = "https://localhost:8080/api/task/create"
      await axios.post(url, payload, config).then(this.submitSuccess).catch(this.submitFailure)
    }
  },
  setup() {
    return {
      v$: useVuelidate()
    }
  },
  data() {
    return {
      errorHandlers: {
        0: () => this.redirectHomeAndToast(
            "Server Error",
            "An unexpected server error has occurred. Please try again later.",
            "danger"
        ),
        400: () => this.appendToast("Form Error", "Invalid form inputs.", "warning"),
        401: () => {
          this.$store.commit("clearToken")
          this.redirectHomeAndToast(
              "Logged Out",
              "Your session has expired. Please log in again.",
              "secondary"
          )
        },
        409: () => this.redirectDashboardAndToast(
            "Task Exists",
            "A similar task is already queued or executing." +
            " Please wait for it to finish before submitting again.",
            "warning"
        ),
        429: () => this.redirectDashboardAndToast(
            "Too Many Active Tasks",
            "You have already reached your limit on the number of active tasks." +
            " Try again later once one of them finishes.",
            "warning"
        )
      },
      task: {
        query: {
          granularity: "file",
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
        granularities: [
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