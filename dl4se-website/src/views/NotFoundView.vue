<template>
  <error-page
      :code="code"
      :title="title"
      :description="description"
  />
</template>

<script>
import ErrorPage from "@/components/ErrorPage";

export default {
  components: { ErrorPage },
  data() {
    return {
      code: 404,
      title: "Page Not Found",
      description: "The requested webpage can not be found, as it does not exist, or has been moved."
    }
  }
}
</script>