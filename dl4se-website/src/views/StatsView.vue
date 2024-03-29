<template>
  <div id="stats">
    <h1 class="page-title">Statistics</h1>
    <b-container>
      <b-row>
        <b-col>
          <b-skeleton-wrapper :loading="loading">
            <template #loading>
              <div class="mb-4">
                <b-skeleton width="100%" />
                <b-skeleton width="100%" />
                <b-skeleton width="100%" class="d-lg-none" />
                <b-skeleton width="100%" class="d-md-none d-lg-none" />
                <b-skeleton width="100%" class="d-md-none d-lg-none" />
                <b-skeleton width="100%" class="d-md-none d-lg-none" />

                <b-skeleton width="33%" class="d-lg-none d-md-block d-none" />
                <b-skeleton width="45%" class="d-xl-none d-lg-block d-none" />
                <b-skeleton width="05%" class="d-xl-block d-md-none" />
              </div>
            </template>
            <template #default>
              <p class="text-justify">
                DL4SE is host to
                <strong v-b-tooltip="count.funcs.toLocaleString()">{{
                  formatNatural(count.funcs)
                }}</strong>
                functions, sourced from
                <strong v-b-tooltip="count.files.toLocaleString()">{{
                  formatNatural(count.files)
                }}</strong>
                files, originating from
                <strong v-b-tooltip="count.repos.toLocaleString()">{{
                  formatNatural(count.repos)
                }}</strong>
                repositories. In total, we have mined
                <strong>{{ formatBytes(size.code) }}</strong> of source code. The platform currently
                has <strong>{{ count.users }}</strong> registered users, and since its inception
                <strong>{{ count.tasks }}</strong> datasets have been constructed. This amounts to
                roughly <strong>{{ formatBytes(size.tasks) }}</strong> in file size.
              </p>
            </template>
          </b-skeleton-wrapper>
        </b-col>
      </b-row>
    </b-container>
    <b-container>
      <b-row align-h="center">
        <b-col cols="12" md="8" lg="6" xl="4">
          <b-bar-chart title="Number of repositories by language" :supplier="reposByLanguage" />
        </b-col>
        <b-col cols="12" md="8" lg="6" xl="4">
          <b-bar-chart title="Number of files by language" :supplier="filesByLanguage" />
        </b-col>
        <b-col cols="12" md="8" lg="6" xl="4">
          <b-bar-chart title="Number of functions by language" :supplier="funcsByLanguage" />
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import formatterMixin from "@/mixins/formatterMixin"
import BBarChart from "@/components/BarChart"

export default {
  components: { BBarChart },
  mixins: [formatterMixin],
  methods: {
    async apiCall(endpoint) {
      return this.$http.get(endpoint).then((res) => res.data)
    },
    async totalUsers() {
      return this.apiCall("/statistics/users")
    },
    async totalRepos() {
      return this.apiCall("/statistics/repos")
    },
    async totalFiles() {
      return this.apiCall("/statistics/files")
    },
    async totalFuncs() {
      return this.apiCall("/statistics/functions")
    },
    async totalTasks() {
      return this.apiCall("/statistics/tasks")
    },
    async totalCodeSize() {
      return this.apiCall("/statistics/code")
    },
    async totalTasksSize() {
      return this.apiCall("/statistics/tasks/size")
    },
    async reposByLanguage() {
      return this.apiCall("/statistics/languages/repos")
    },
    async filesByLanguage() {
      return this.apiCall("/statistics/languages/files")
    },
    async funcsByLanguage() {
      return this.apiCall("/statistics/languages/functions")
    }
  },
  async mounted() {
    await Promise.all([
      this.totalUsers(),
      this.totalRepos(),
      this.totalFiles(),
      this.totalFuncs(),
      this.totalTasks(),
      this.totalCodeSize(),
      this.totalTasksSize()
    ])
      .then(
        ([
          totalUsers,
          totalRepos,
          totalFiles,
          totalFunctions,
          totalTasks,
          totalCodeSize,
          totalTaskSize
        ]) => {
          this.count.users = totalUsers
          this.count.repos = totalRepos
          this.count.files = totalFiles
          this.count.funcs = totalFunctions
          this.count.tasks = totalTasks

          this.size.code = totalCodeSize
          this.size.tasks = totalTaskSize

          this.loading = false
        }
      )
      .catch(() => {
        // TODO 03.11.22: Migrate this to a router guard?
        this.$router.replace({ name: "home" })
      })
  },
  data() {
    return {
      loading: true,
      count: {
        users: undefined,
        repos: undefined,
        files: undefined,
        funcs: undefined,
        tasks: undefined
      },
      size: {
        code: undefined,
        tasks: undefined
      }
    }
  }
}
</script>
