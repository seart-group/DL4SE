<template>
  <div id="task" v-if="show">
    <h1 class="d-none">Create Code Dataset</h1>
    <b-form @submit.prevent.stop="submit" class="container bg-light border">
      <b-form-row>
        <b-col>
          <h2>Repository Sample Characteristics</h2>
        </b-col>
      </b-form-row>
      <b-form-row>
        <b-form-group :state="!v$.task.query.language_name.$invalid" class="col-12">
          <b-dropdown-select
            id="language-select"
            placeholder="Language"
            :options="options.languages"
            v-model="task.query.language_name"
          >
            <template #header>Choose a language</template>
          </b-dropdown-select>
          <template #invalid-feedback>Language is required</template>
        </b-form-group>
        <b-form-group label="Commits:" class="col-12 col-sm-6 col-md-3">
          <b-counter
            id="commits-input"
            placeholder="min"
            v-model.number="task.query.min_commits"
            :state="validate(v$.task.query.min_commits)"
            :min="0"
          />
        </b-form-group>
        <b-form-group label="Issues:" class="col-12 col-sm-6 col-md-3">
          <b-counter
            id="issues-input"
            placeholder="min"
            v-model.number="task.query.min_issues"
            :state="validate(v$.task.query.min_issues)"
            :min="0"
          />
        </b-form-group>
        <b-form-group label="Contributors:" class="col-12 col-sm-6 col-md-3">
          <b-counter
            id="contributors-input"
            placeholder="min"
            v-model.number="task.query.min_contributors"
            :state="validate(v$.task.query.min_contributors)"
            :min="0"
          />
        </b-form-group>
        <b-form-group label="Stars:" class="col-12 col-sm-6 col-md-3">
          <b-counter
            id="stars-input"
            placeholder="min"
            v-model.number="task.query.min_stars"
            :state="validate(v$.task.query.min_stars)"
            :min="0"
          />
        </b-form-group>
        <b-form-group class="col-12">
          <b-checkbox id="license-checkbox" v-model="task.query.has_license" :inline="$screen.sm">
            Has Open-source License
          </b-checkbox>
          <b-checkbox id="forks-checkbox" v-model="task.query.exclude_forks" :inline="$screen.sm">
            Exclude Forks
          </b-checkbox>
        </b-form-group>
      </b-form-row>
      <b-form-row>
        <b-col>
          <hr />
          <h2>Dataset Characteristics</h2>
        </b-col>
      </b-form-row>
      <b-form-row class="column-gap-3">
        <b-form-group class="col-12 col-md-auto">
          <template #label>
            Granularity
            <b-documentation-link page="docs" section="#granularity" tabindex="-1" target="_blank" />
          </template>
          <b-form-radio-group
            id="type-radio"
            v-model="task.query.granularity"
            :options="options.granularities"
            :stacked="$screen.md"
            required
          />
        </b-form-group>
        <b-form-group class="col-12 col-md">
          <template #label>
            Metadata
            <b-documentation-link page="docs" section="#meta" tabindex="-1" target="_blank" />
          </template>
          <b-form-checkbox id="sex-checkbox" v-model="task.processing.include_symbolic_expression">
            Pair each instance with its Symbolic Expression representation
          </b-form-checkbox>
          <b-form-text v-show="task.processing.include_symbolic_expression">
            Choosing to include S-Expressions in your dataset will increase the size of the exported file.
          </b-form-text>
          <b-form-checkbox id="ast-checkbox" v-model="task.processing.include_ast">
            Pair each instance with its AST-based representation
          </b-form-checkbox>
          <b-form-text v-show="task.processing.include_ast">
            Choosing to include ASTs in your dataset will <strong>drastically</strong> increase the size of the exported
            file.
          </b-form-text>
          <b-form-checkbox id="ts-checkbox" v-model="task.processing.include_tree_sitter_version">
            Pair each instance with <code>tree-sitter</code> parser metadata
          </b-form-checkbox>
          <b-form-text v-show="task.processing.include_tree_sitter_version">
            Enabling this will include the version of the <code>tree-sitter</code> parser which was used to compute all
            the instance information. This meta-information is used primarily for troubleshooting, and as such is
            unlikely to benefit the average user. For this reason we recommend keeping it turned off.
          </b-form-text>
        </b-form-group>
      </b-form-row>
      <b-form-row>
        <b-col>
          <hr />
          <h2>Code Filters &amp; Processing</h2>
        </b-col>
      </b-form-row>
      <b-row>
        <b-col cols="12" md="6">
          <b-form-row>
            <b-form-group label="Characters:" class="col-12 col-sm-6">
              <b-counter
                id="characters-input-min"
                placeholder="min"
                v-model.number="task.query.min_characters"
                :min="0"
                :max="coalesceMax(task.query.max_characters)"
                :state="validate(v$.task.query.min_characters)"
              />
            </b-form-group>
            <b-form-group class="col-12 col-sm-6 align-self-end">
              <b-counter
                id="characters-input-max"
                placeholder="max"
                v-model.number="task.query.max_characters"
                :min="coalesceMin(task.query.min_characters)"
                :state="validate(v$.task.query.max_characters)"
              />
            </b-form-group>
            <b-form-group label="Tokens:" class="col-12 col-sm-6">
              <b-counter
                id="tokens-input-min"
                placeholder="min"
                v-model.number="task.query.min_tokens"
                :min="0"
                :max="coalesceMax(task.query.max_tokens)"
                :state="validate(v$.task.query.min_tokens)"
              />
            </b-form-group>
            <b-form-group class="col-12 col-sm-6 align-self-end">
              <b-counter
                id="tokens-input-max"
                placeholder="max"
                v-model.number="task.query.max_tokens"
                :min="coalesceMin(task.query.min_tokens)"
                :state="validate(v$.task.query.max_tokens)"
              />
            </b-form-group>
            <b-form-group label="Lines:" class="col-12 col-sm-6">
              <b-counter
                id="lines-input-min"
                placeholder="min"
                v-model.number="task.query.min_lines"
                :min="0"
                :max="coalesceMax(task.query.max_lines)"
                :state="validate(v$.task.query.min_lines)"
              />
            </b-form-group>
            <b-form-group class="col-12 col-sm-6 align-self-end">
              <b-counter
                id="lines-input-max"
                placeholder="max"
                v-model.number="task.query.max_lines"
                :min="coalesceMin(task.query.min_lines)"
                :state="validate(v$.task.query.max_lines)"
              />
            </b-form-group>
          </b-form-row>
        </b-col>
        <b-col cols="12" md="6">
          <b-form-row class="column-gap-3">
            <b-form-group class="col-12">
              <template #label>
                Exclude
                <b-documentation-link page="docs" section="#exclusion" tabindex="-1" target="_blank" />
              </template>
              <b-checkbox id="test-code-checkbox" v-model="task.query.exclude_test">Test code</b-checkbox>
              <b-checkbox
                id="boilerplate-code-checkbox"
                v-if="task.query.granularity === 'function'"
                v-model="task.query.exclude_boilerplate"
              >
                Boilerplate code
              </b-checkbox>
              <b-checkbox id="unparsable-code-checkbox" v-model="task.query.exclude_errors">
                Instances with syntax errors
              </b-checkbox>
              <b-checkbox id="non-ascii-checkbox" v-model="task.query.exclude_non_ascii">
                Instances with non-ASCII characters
              </b-checkbox>
            </b-form-group>
            <b-form-group class="col-12 col-sm-auto">
              <template #label>
                Ignore
                <b-documentation-link page="docs" section="#duplicates-and-clones" tabindex="-1" target="_blank" />
              </template>
              <b-checkbox id="duplicates-checkbox" v-model="task.query.exclude_duplicates">Duplicates</b-checkbox>
              <b-checkbox id="clones-checkbox" v-model="task.query.exclude_identical">Near-clones</b-checkbox>
            </b-form-group>
            <b-form-group class="col-12 col-sm">
              <template #label>
                Remove
                <b-documentation-link page="docs" section="#processing" tabindex="-1" target="_blank" />
              </template>
              <b-checkbox id="comments-checkbox" v-model="task.processing.remove_regular_comments">
                Regular comments
              </b-checkbox>
              <b-checkbox id="docstring-checkbox" v-model="task.processing.remove_documentation_comments">
                Documentation comments
              </b-checkbox>
            </b-form-group>
          </b-form-row>
        </b-col>
      </b-row>
      <b-form-row>
        <b-form-group class="col-12 d-flex justify-content-center">
          <b-form-submit :disabled="v$.$invalid" />
        </b-form-group>
      </b-form-row>
    </b-form>
  </div>
</template>

<script>
import routerMixin from "@/mixins/routerMixin";
import bootstrapMixin from "@/mixins/bootstrapMixin";
import useVuelidate from "@vuelidate/core";
import { between, minValue, required } from "@vuelidate/validators";
import BCounter from "@/components/Counter";
import BDocumentationLink from "@/components/DocumentationLink";
import BDropdownSelect from "@/components/DropdownSelect";
import BFormSubmit from "@/components/FormSubmit";

export default {
  components: {
    BCounter,
    BDocumentationLink,
    BDropdownSelect,
    BFormSubmit,
  },
  mixins: [routerMixin, bootstrapMixin],
  props: {
    uuid: String,
  },
  watch: {
    "task.query.granularity": {
      handler() {
        if (this.task.query.granularity === "file") this.task.query.exclude_boilerplate = false;
      },
    },
    "task.query.exclude_duplicates": {
      handler() {
        if (this.task.query.exclude_duplicates && this.task.query.exclude_identical)
          this.task.query.exclude_identical = false;
      },
    },
    "task.query.exclude_identical": {
      handler() {
        if (this.task.query.exclude_identical && this.task.query.exclude_duplicates)
          this.task.query.exclude_duplicates = false;
      },
    },
  },
  methods: {
    coalesceMin(value) {
      return value || 0;
    },
    coalesceMax(value) {
      return value || Number.MAX_SAFE_INTEGER;
    },
    validate(el$) {
      const valid = !el$.$invalid;
      const value = el$.$model;
      const nullish = value === undefined || value === null;
      return !nullish ? valid : null;
    },
    submitSuccess() {
      this.redirectDashboardAndToast(
        "Task Created",
        `
        Your dataset creation request has been accepted.
        Please note that it may take some time until it begins executing.
        You will receive an email notification once the dataset is compiled.
        `,
        "secondary",
      );
    },
    submitFailure(err) {
      const status = err.response.status;
      switch (status) {
        case 400:
          this.appendToast("Form Error", "Invalid form inputs.", "warning");
          break;
        case 401:
          this.$store.dispatch("logOut").then(() => {
            this.appendToast("Login Required", "Your session has expired. Please log in again.", "secondary");
          });
          break;
        case 409:
          this.redirectDashboardAndToast(
            "Task Exists",
            `
            A similar task is already queued or executing.
            Please wait for it to finish before submitting again.
            `,
            "warning",
          );
          break;
        case 429:
          this.redirectDashboardAndToast(
            "Too Many Active Tasks",
            `
            You have already reached your limit on the number of active tasks.
            Try again later once one of them finishes.
            `,
            "warning",
          );
          break;
        default:
          this.appendToast(
            "Server Error",
            "An unexpected server error has occurred. Please try again later.",
            "danger",
          );
          break;
      }
    },
    async submit() {
      await this.$http.post("/task/code/create", this.task).then(this.submitSuccess).catch(this.submitFailure);
    },
    async getLanguages() {
      await this.$http.get("/language").then(({ data }) => (this.options.languages = data));
    },
    async getParameters() {
      if (this.uuid) {
        await this.$http(`/task/${this.uuid}`)
          .then(({ data }) => {
            Object.assign(this.task.query, data.query);
            Object.assign(this.task.processing, data.processing);
          })
          .catch((err) => {
            const status = err.response.status;
            switch (status) {
              case 400:
                this.redirectDashboardAndToast(
                  "Invalid UUID",
                  "The specified task UUID is not valid. Make sure you copied the link correctly, and try again.",
                  "warning",
                );
                break;
              case 401:
                this.$store.dispatch("logOut").then(() => {
                  this.appendToast("Login Required", "Your session has expired. Please log in again.", "secondary");
                });
                break;
              case 403:
                break;
              case 404:
                this.redirectDashboardAndToast("Task Not Found", "The specified task could not be found.", "warning");
                break;
              default:
                this.$router.push({ name: "home" });
                break;
            }
          });
      }
    },
  },
  setup() {
    return {
      v$: useVuelidate({ $autoDirty: true }),
    };
  },
  async mounted() {
    await this.getLanguages();
    await this.getParameters();
    this.show = true;
  },
  data() {
    return {
      show: false,
      task: {
        query: {
          granularity: "file",
          language_name: null,
          has_license: false,
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
          exclude_forks: false,
          exclude_duplicates: false,
          exclude_identical: false,
          exclude_test: false,
          exclude_non_ascii: false,
          exclude_errors: false,
          exclude_boilerplate: false,
        },
        processing: {
          include_ast: false,
          include_symbolic_expression: false,
          include_tree_sitter_version: false,
          remove_documentation_comments: false,
          remove_regular_comments: false,
        },
      },
      options: {
        granularities: [
          {
            text: "File",
            value: "file",
          },
          {
            text: "Function",
            value: "function",
          },
        ],
        languages: [],
      },
    };
  },
  validations() {
    return {
      task: {
        query: {
          language_name: {
            required: required,
          },
          min_commits: {
            value: minValue(0),
          },
          min_issues: {
            value: minValue(0),
          },
          min_contributors: {
            value: minValue(0),
          },
          min_stars: {
            value: minValue(0),
          },
          min_characters: {
            value: between(0, this.task.query.max_characters || Number.MAX_SAFE_INTEGER),
          },
          max_characters: {
            value: between(this.task.query.min_characters || 0, Number.MAX_SAFE_INTEGER),
          },
          min_tokens: {
            value: between(0, this.task.query.max_tokens || Number.MAX_SAFE_INTEGER),
          },
          max_tokens: {
            value: between(this.task.query.min_tokens || 0, Number.MAX_SAFE_INTEGER),
          },
          min_lines: {
            value: between(0, this.task.query.max_lines || Number.MAX_SAFE_INTEGER),
          },
          max_lines: {
            value: between(this.task.query.min_lines || 0, Number.MAX_SAFE_INTEGER),
          },
        },
      },
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/view/code.sass" />
