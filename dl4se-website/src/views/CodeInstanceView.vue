<template>
  <div :id="`code-${id}`" v-show="show">
    <b-container>
      <b-row>
        <b-col>
          <h1 v-if="!code" class="text-center">Instance could not be found!</h1>
          <b-card v-else class="rounded-0" no-body>
            <b-tabs :align="tabsAlign" card>
              <b-tab title="Description" active>
                <b-table-simple :sticky-header="tableHeight" class="m-0" responsive>
                  <b-tr>
                    <b-td>Repository:</b-td>
                    <b-td class="text-monospace">
                      <b-link :href="repoURL" target="_blank">
                        {{ code.repo.name }}
                      </b-link>
                    </b-td>
                  </b-tr>
                  <b-tr>
                    <b-td>Last Commit SHA:</b-td>
                    <b-td class="text-monospace">
                      <b-link :href="repoTreeURL" target="_blank">
                        <template v-if="!$screen.md">
                          {{ code.repo.last_commit_sha.substr(0, 7) }}
                        </template>
                        <template v-else>
                          {{ code.repo.last_commit_sha }}
                        </template>
                      </b-link>
                    </b-td>
                  </b-tr>
                  <b-tr>
                    <b-td>Last Commit Date:</b-td>
                    <b-td>{{ code.repo.last_commit + "Z" }}</b-td>
                  </b-tr>
                  <b-tr>
                    <b-td>Path:</b-td>
                    <b-td class="text-monospace">
                      <b-link :href="repoFileURL" target="_blank">
                        {{ filePath }}
                      </b-link>
                    </b-td>
                  </b-tr>
                  <b-tr>
                    <b-td>Language:</b-td>
                    <b-td>{{ code.language_name }}</b-td>
                  </b-tr>
                  <b-tr>
                    <b-td>Characters:</b-td>
                    <b-td>{{ code.characters }}</b-td>
                  </b-tr>
                  <b-tr>
                    <b-td>Code Tokens:</b-td>
                    <b-td>{{ code.code_tokens }}</b-td>
                  </b-tr>
                  <b-tr>
                    <b-td>Total Tokens:</b-td>
                    <b-td>{{ code.total_tokens }}</b-td>
                  </b-tr>
                  <b-tr>
                    <b-td>Lines:</b-td>
                    <b-td>{{ code.lines }}</b-td>
                  </b-tr>
                  <b-tr>
                    <b-td :colspan="!hasLicense ? 2 : null">License:</b-td>
                    <b-td v-if="hasLicense">{{ code.repo.license }}</b-td>
                  </b-tr>
                  <b-tr>
                    <b-td>Content Hash:</b-td>
                    <b-td class="text-monospace">
                      <b-abbr v-if="!$screen.lg" :title="code.content_hash">
                        {{ code.content_hash.substr(0, 7) }}
                      </b-abbr>
                      <template v-else>
                        {{ code.content_hash }}
                      </template>
                    </b-td>
                  </b-tr>
                  <b-tr>
                    <b-td>AST Hash:</b-td>
                    <b-td class="text-monospace">
                      <b-abbr v-if="!$screen.lg" :title="code.ast_hash">
                        {{ code.ast_hash.substr(0, 7) }}
                      </b-abbr>
                      <template v-else>
                        {{ code.ast_hash }}
                      </template>
                    </b-td>
                  </b-tr>
                </b-table-simple>
              </b-tab>
              <b-tab title="Source">
                <b-highlighted-code :language="language" :code="code.content" />
              </b-tab>
              <b-tab title="AST">
                <b-highlighted-code language="xml" :code="formatXML(code.ast)" />
              </b-tab>
            </b-tabs>
          </b-card>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import BAbbr from "@/components/Abbr";
import BHighlightedCode from "@/components/HighlightedCode";
import formatterMixin from "@/mixins/formatterMixin";

const base = "https://github.com";

export default {
  components: { BAbbr, BHighlightedCode },
  mixins: [formatterMixin],
  props: {
    id: {
      type: Number,
      required: true,
      validator(value) {
        return value > 0;
      },
    },
  },
  computed: {
    tabsAlign() {
      return !this.$screen.md ? "center" : "left";
    },
    tableHeight() {
      return `${this.$screen.md ? 500 : 510}px`;
    },
    hasLicense() {
      return !!this.code.repo.license;
    },
    filePath() {
      return this.code.file_path ?? this.code.path;
    },
    repoURL() {
      return `${base}/${this.code.repo.name}`;
    },
    repoTreeURL() {
      return `${this.repoURL}/tree/${this.code.repo.last_commit_sha}`;
    },
    repoFileURL() {
      return `${this.repoTreeURL}/${this.filePath}`;
    },
    language() {
      return this.code.language_name.toLowerCase();
    },
  },
  beforeMount() {
    this.$http
      .get(`/code/${this.id}`)
      .then(({ data }) => data)
      .then((code) => (this.code = code))
      .catch((err) => {
        const status = err.response.status;
        switch (status) {
          case 401:
            this.$store.dispatch("logOut").then(() => {
              this.appendToast("Login Required", "Your session has expired. Please log in again.", "secondary");
            });
            break;
          case 404:
            break;
          default:
            this.$router.push({ name: "home" });
            break;
        }
      })
      .finally(() => (this.show = true));
  },
  data() {
    return {
      show: false,
      code: undefined,
    };
  },
  head() {
    return {
      title: `Instance [${this.id}]`,
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/view/code-instance.sass" />
