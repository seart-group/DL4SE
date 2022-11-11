<template>
  <div id="task" v-if="show">
    <h1 class="page-title">Specify your dataset</h1>
    <div class="task-create-form">
      <b-container class="task-create-form-section-top">
        <b-row>
          <b-col>
            <h5 class="task-create-form-section-title">Repository Sample Characteristics</h5>
          </b-col>
        </b-row>
        <b-row class="justify-content-md-between align-items-xl-center">
          <b-col xl="5" lg="4" md="5" sm="12">
            <b-row no-gutters align-h="between" align-v="center">
              <b-col xl="4" lg="12">
                <b-form-group :state="dropdownState" class="m-0 py-2">
                  <b-dropdown-select id="language-select"
                                     placeholder="Language"
                                     header="Select a language"
                                     :options="options.languages"
                                     v-model="task.query.language_name"
                                     required
                  />
                  <template #invalid-feedback>
                    Language is required
                  </template>
                </b-form-group>
              </b-col>
              <b-col xl="7" lg="12">
                <b-form-group class="m-0 py-2">
                  <b-checkbox id="license-checkbox" v-model="task.query.has_license" inline>
                    Has Open-source License
                  </b-checkbox>
                  <b-checkbox id="forks-checkbox" v-model="task.query.exclude_forks" inline>
                    Exclude Forks
                  </b-checkbox>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col xl="7" lg="8" md="6" sm="12">
            <b-row no-gutters class="justify-content-lg-around">
              <b-col lg="6" md="12">
                <b-range id="commits-range" field="commits" v-model="commits" lower-bound :min="0" />
              </b-col>
              <b-col lg="6" md="12">
                <b-range id="issues-range" field="issues" v-model="contributors" lower-bound :min="0" />
              </b-col>
              <b-col lg="6" md="12">
                <b-range id="contributors-range" field="contributors" v-model="issues" lower-bound :min="0" />
              </b-col>
              <b-col lg="6" md="12">
                <b-range id="stars-range" field="stars" v-model="stars" lower-bound :min="0" />
              </b-col>
            </b-row>
          </b-col>
        </b-row>
      </b-container>
      <b-container class="py-4 task-create-form-section-middle">
        <b-row>
          <b-col>
            <h5 class="task-create-form-section-title">Dataset Characteristics</h5>
          </b-col>
        </b-row>
        <b-row>
          <b-col xl="2" lg="2" md="3" cols="12">
            <b-form-group label-class="font-weight-bold" class="m-0 pb-2 pb-md-0">
              <template #label>
                Granularity
                <b-link :to="{ name: 'docs', hash: '#granularity' }" target="_blank" class="text-dark" tabindex="-1">
                  <b-icon-question-circle-fill />
                </b-link>
              </template>
              <b-form-radio-group id="type-radio" required
                                  v-model="task.query.granularity"
                                  :options="options.granularities"
              />
            </b-form-group>
          </b-col>
          <b-col xl="5" lg="6" md="9" cols="12">
            <b-form-group label-class="font-weight-bold" class="m-0">
              <template #label>
                AST
                <b-link :to="{ name: 'docs', hash: '#ast' }" target="_blank" class="text-dark" tabindex="-1">
                  <b-icon-question-circle-fill />
                </b-link>
              </template>
              <b-form-checkbox id="ast-checkbox" v-model="task.query.include_ast">
                Pair each instance with its AST-based representation
              </b-form-checkbox>
              <b-form-text v-show="task.query.include_ast" class="pl-4">
                Choosing to include ASTs in your dataset will <strong>drastically</strong> increase the size of the
                exported file, and may increase the amount of time needed to export individual instances if processing
                is applied.
              </b-form-text>
            </b-form-group>
          </b-col>
        </b-row>
      </b-container>
      <b-container class="pb-4 task-create-form-section-middle">
        <b-row>
          <b-col>
            <h5 class="task-create-form-section-title">Instance Filters</h5>
          </b-col>
        </b-row>
        <b-row align-h="center">
          <b-col lg="3" md="6" sm="12">
            <b-form-group label-class="font-weight-bold">
              <template #label>
                Exclude
                <b-link :to="{ name: 'docs', hash: '#exclusion' }" target="_blank" class="text-dark" tabindex="-1">
                  <b-icon-question-circle-fill />
                </b-link>
              </template>
              <b-checkbox id="test-code-checkbox" v-model="task.query.exclude_test">
                Test code
              </b-checkbox>
              <b-checkbox id="boilerplate-code-checkbox"
                          v-if="task.query.granularity === 'function'"
                          v-model="task.query.exclude_boilerplate"
              >
                Boilerplate code
              </b-checkbox>
              <b-checkbox id="unparsable-code-checkbox"
                          v-if="task.query.granularity === 'file'"
                          v-model="task.query.exclude_unparsable"
              >
                Unparsable code
              </b-checkbox>
              <b-checkbox id="non-ascii-checkbox" v-model="task.query.exclude_non_ascii">
                Instances with non-ASCII characters
              </b-checkbox>
            </b-form-group>
          </b-col>
          <b-col lg="2" md="6" sm="12">
            <b-form-group label-class="font-weight-bold">
              <template #label>
                Ignore
                <b-link :to="{ name: 'docs', hash: '#duplicates-and-clones' }" target="_blank" class="text-dark" tabindex="-1">
                  <b-icon-question-circle-fill />
                </b-link>
              </template>
              <b-checkbox id="duplicates-checkbox" v-model="task.query.exclude_duplicates" inline>
                Duplicates
              </b-checkbox>
              <b-checkbox id="clones-checkbox" v-model="task.query.exclude_identical" inline>
                Near-clones
              </b-checkbox>
            </b-form-group>
          </b-col>
          <b-col lg="7" md="9" sm="12">
            <b-range id="characters-range" field="characters" lower-bound upper-bound :min="0" v-model="characters" />
            <b-range id="tokens-range" field="tokens" lower-bound upper-bound :min="0" v-model="tokens" />
            <b-range id="lines-range" field="lines" lower-bound upper-bound :min="0" v-model="lines" />
          </b-col>
        </b-row>
      </b-container>
      <b-container class="task-create-form-section-middle">
        <b-row>
          <b-col>
            <h5 class="task-create-form-section-title">Instance Processing</h5>
          </b-col>
        </b-row>
        <b-row align-h="between">
          <b-col xl="7" lg="7" md="6" sm="6" cols="12">
            <b-row no-gutters>
              <b-col xl="3" lg="4" md="12" sm="12" cols="12">
                <b-form-group label-class="font-weight-bold">
                  <template #label>
                    Remove
                    <b-link :to="{ name: 'docs', hash: '#comment-removal' }" target="_blank" class="text-dark" tabindex="-1">
                      <b-icon-question-circle-fill />
                    </b-link>
                  </template>
                  <b-checkbox id="docstring-checkbox" v-model="task.processing.remove_docstring" inline>
                    Docstrings
                  </b-checkbox>
                  <b-checkbox id="comments-checkbox" v-model="task.processing.remove_inner_comments" inline>
                    Inner comments
                  </b-checkbox>
                </b-form-group>
              </b-col>
              <b-col xl="6" lg="7" md="12" sm="12" cols="12" offset-lg="1" offset-xl="2">
                <b-form-group label-class="font-weight-bold">
                  <template #label>
                    Mask
                    <b-link :to="{ name: 'docs', hash: '#masking' }" target="_blank" class="text-dark" tabindex="-1">
                      <b-icon-question-circle-fill />
                    </b-link>
                  </template>
                  <b-masking id="token-mask" v-model="masking" />
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col xl="5" lg="5" md="6" sm="6" cols="12">
            <b-form-group label-class="font-weight-bold">
              <template #label>
                Abstract
                <b-link :to="{ name: 'docs', hash: '#abstraction' }" target="_blank" class="text-dark" tabindex="-1">
                  <b-icon-question-circle-fill />
                </b-link>
              </template>
              <b-form-checkbox id="abstract-enabled" v-model="task.processing.abstract_code">
                Abstract instances using the following idioms:
              </b-form-checkbox>
              <b-tag-select id="abstract-idioms"
                            placeholder="Idiom..."
                            v-model="task.processing.abstract_idioms"
                            :disabled="!task.processing.abstract_code"
              />
            </b-form-group>
          </b-col>
        </b-row>
      </b-container>
      <b-container class="py-4 task-create-form-section-bottom">
        <b-row align-h="center">
          <b-col cols="auto">
            <b-button :disabled="v$.$invalid" @click="submit" class="btn-secondary-border-2">Generate Dataset</b-button>
          </b-col>
        </b-row>
      </b-container>
    </div>
  </div>
</template>

<script>
import routerMixin from "@/mixins/routerMixin"
import bootstrapMixin from "@/mixins/bootstrapMixin"
import useVuelidate from "@vuelidate/core"
import BDropdownSelect from "@/components/DropdownSelect"
import BMasking from "@/components/Masking"
import BRange from "@/components/Range"
import BTagSelect from "@/components/TagSelect"

export default {
  components: {
    BDropdownSelect,
    BMasking,
    BRange,
    BTagSelect
  },
  mixins: [ routerMixin, bootstrapMixin ],
  props: {
    uuid: String
  },
  computed: {
    dropdownState() {
      const child$ = this.v$.$getResultsForChild("language-select")
      return (child$) ? !child$.$invalid : null
    },
    commits: {
      get() {
        return { lower: this.task.query.min_commits }
      },
      set(value) {
        this.task.query.min_commits = value.lower
      }
    },
    contributors: {
      get() {
        return { lower: this.task.query.min_contributors }
      },
      set(value) {
        this.task.query.min_contributors = value.lower
      }
    },
    issues: {
      get() {
        return { lower: this.task.query.min_issues }
      },
      set(value) {
        this.task.query.min_issues = value.lower
      }
    },
    stars: {
      get() {
        return { lower: this.task.query.min_stars }
      },
      set(value) {
        this.task.query.min_stars = value.lower
      }
    },
    characters: {
      get() {
        return {
          lower: this.task.query.min_characters,
          upper: this.task.query.max_characters
        }
      },
      set(value) {
        this.task.query.min_characters = value.lower
        this.task.query.max_characters = value.upper
      }
    },
    tokens: {
      get() {
        return {
          lower: this.task.query.min_tokens,
          upper: this.task.query.max_tokens
        }
      },
      set(value) {
        this.task.query.min_tokens = value.lower
        this.task.query.max_tokens = value.upper
      }
    },
    lines: {
      get() {
        return {
          lower: this.task.query.min_lines,
          upper: this.task.query.max_lines
        }
      },
      set(value) {
        this.task.query.min_lines = value.lower
        this.task.query.max_lines = value.upper
      }
    },
    masking: {
      get() {
        return {
          token: this.task.processing.mask_token,
          percentage: this.task.processing.mask_percentage,
          contiguousOnly: this.task.processing.mask_contiguous_only
        }
      },
      set(value) {
        this.task.processing.mask_token = value.token
        this.task.processing.mask_percentage = value.percentage
        this.task.processing.mask_contiguous_only = value.contiguousOnly
      }
    }
  },
  watch: {
    "task.query.exclude_duplicates": {
      handler() {
        if (this.task.query.exclude_duplicates && this.task.query.exclude_identical)
          this.task.query.exclude_identical = false
      }
    },
    "task.query.exclude_identical": {
      handler() {
        if (this.task.query.exclude_identical && this.task.query.exclude_duplicates)
          this.task.query.exclude_duplicates = false
      }
    }
  },
  methods: {
    submitSuccess() {
      this.redirectDashboardAndToast(
          "Task Created",
          "Your dataset creation request has been accepted. " +
          "Please note that it may take some time until it begins executing. " +
          "You will receive an email notification once the dataset is compiled.",
          "secondary"
      )
    },
    submitFailure(err) {
      const status = err.response.status
      switch (status) {
        case 400:
          this.appendToast("Form Error", "Invalid form inputs.", "warning")
          break
        case 401:
          this.$store.dispatch("logOut").then(() => {
            this.appendToast(
                "Login Required",
                "Your session has expired. Please log in again.",
                "secondary"
            )
          })
          break
        case 409:
          this.redirectDashboardAndToast(
              "Task Exists",
              "A similar task is already queued or executing." +
              " Please wait for it to finish before submitting again.",
              "warning"
          )
          break
        case 429:
          this.redirectDashboardAndToast(
              "Too Many Active Tasks",
              "You have already reached your limit on the number of active tasks." +
              " Try again later once one of them finishes.",
              "warning"
          )
          break
        default:
          this.appendToast("Server Error", "An unexpected server error has occurred. Please try again later.", "danger")
          break
      }
    },
    async submit() {
      const endpoint = "/task/create"
      const payload = this.task
      await this.$http.post(endpoint, payload)
          .then(this.submitSuccess)
          .catch(this.submitFailure)
    },
    async getLanguages() {
      const endpoint = "/language"
      await this.$http.get(endpoint)
          .then((res) => {
            this.options.languages = res.data
          })
    },
    async getParameters() {
      if (this.uuid) {
        const endpoint = `/task/${this.uuid}`;
        await this.$http(endpoint)
            .then((res) => {
              const task = res.data
              Object.assign(this.task.query, task.query)
              Object.assign(this.task.processing, task.processing)
            }).catch((err) => {
              const status = err.response.status
              switch (status) {
                case 400:
                  this.redirectDashboardAndToast(
                      "Invalid UUID",
                      "The specified task UUID is not valid. Make sure you copied the link correctly, and try again.",
                      "warning"
                  )
                  break
                case 401:
                  this.$store.dispatch("logOut").then(() => {
                    this.appendToast(
                        "Login Required",
                        "Your session has expired. Please log in again.",
                        "secondary"
                    )
                  })
                  break
                case 404:
                  this.redirectDashboardAndToast("Task Not Found", "The specified task could not be found.", "warning")
                  break
                default:
                  this.$router.push({ name: 'home' })
                  break
              }
            })
      }
    }
  },
  setup() {
    return {
      v$: useVuelidate()
    }
  },
  async mounted() {
    await this.getLanguages()
    await this.getParameters()
    this.show = true
  },
  data() {
    return {
      show: false,
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
          include_ast: false,
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
          abstract_code: false,
          abstract_idioms : []
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
        languages: [],
      }
    }
  }
}
</script>