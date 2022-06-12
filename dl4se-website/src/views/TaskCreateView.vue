<template>
  <div id="task">
    <h1 class="page-title">Specify your dataset</h1>
    <b-section-repo class="task-form-section-top" :options="options.languages"
                    :language="language" @update:language="language = $event"
                    :has-license="has_license" @update:has-license="has_license = $event"
                    :exclude="exclude" @update:exclude="exclude = $event"
                    :commits="commits" @update:commits="commits = $event"
                    :contributors="contributors" @update:contributors="contributors = $event"
                    :issues="issues" @update:issues="issues = $event"
                    :stars="stars" @update:stars="stars = $event"
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
            <b-form-radio-group id="type-radio" required v-model="type" :options="options.types" />
          </b-form-group>
        </b-col>
      </b-row>
    </b-container>
    <b-section-filters-code class="pb-4 task-form-section-middle" :granularity="type"
                            :exclude="exclude" @update:exclude="exclude = $event"
                            :characters="characters" @update:characters="characters = $event"
                            :tokens="tokens" @update:tokens="tokens = $event"
                            :lines="lines" @update:lines="lines = $event"
    />
    <b-section-processing-code class="task-form-section-middle"
                               :remove="remove" @update:remove="remove = $event"
                               :masking="masking" @update:masking="masking = $event"
                               :idioms="idioms" @update:idioms="idioms = $event"
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
      const payload = {
        query: {},
        processing: {}
      }

      payload.query["type"] = this.type
      payload.query["language_name"] = this.language
      payload.query["has_license"] = this.has_license
      payload.query["exclude_forks"] = this.exclude.forks
      payload.query["min_commits"] = this.commits.lower || 0
      payload.query["min_contributors"] = this.contributors.lower || 0
      payload.query["min_issues"] = this.issues.lower || 0
      payload.query["min_stars"] = this.stars.lower || 0
      payload.query["include_ast"] = this.include.ast
      payload.query["min_tokens"] = this.tokens.lower
      payload.query["max_tokens"] = this.tokens.upper
      payload.query["min_lines"] = this.lines.lower
      payload.query["max_lines"] = this.lines.upper
      payload.query["min_characters"] = this.characters.lower
      payload.query["max_characters"] = this.characters.upper
      payload.query["exclude_duplicates"] = this.exclude.duplicates
      payload.query["exclude_identical"] = this.exclude.identical
      payload.query["exclude_test"] = this.exclude.test
      payload.query["exclude_non_ascii"] = this.exclude.non_ascii
      if (this.type === "file") {
        payload.query["exclude_unparsable"] = this.exclude.unparsable
      } else {
        payload.query["exclude_boilerplate"] = this.exclude.boilerplate
      }

      payload.processing["remove_docstring"] = this.remove.docstring
      payload.processing["remove_inner_comments"] = this.remove.inner_comments
      payload.processing["mask_token"] = this.masking.token
      payload.processing["mask_percentage"] = this.masking.percentage
      payload.processing["mask_contiguous_only"] = this.masking.contiguous_only
      payload.processing["idioms"] = this.idioms

      const config = {
        headers : {
          'authorization': this.$store.getters.getToken
        }
      }

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
      },
      type : "file",
      language: null,
      has_license : false,
      commits: {
        lower: null,
        upper: null
      },
      contributors: {
        lower: null,
        upper: null
      },
      issues: {
        lower: null,
        upper: null
      },
      stars: {
        lower: null,
        upper: null
      },
      characters: {
        lower: null,
        upper: null
      },
      tokens: {
        lower: null,
        upper: null
      },
      lines: {
        lower: null,
        upper: null
      },
      exclude: {
        forks: false,
        identical : false,
        duplicates: false,
        test : false,
        non_ascii : false,
        boilerplate : false,
        unparsable: false
      },
      include: {
        ast: true
      },
      masking: {
        token: null,
        percentage: null,
        contiguous_only : null
      },
      remove: {
        docstring: false,
        inner_comments: false
      },
      idioms: []
    }
  }
}
</script>