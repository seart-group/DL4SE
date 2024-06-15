<template>
  <not-found />
</template>

<script>
import NotFound from "@/partials/NotFound";

export default {
  components: { NotFound },
  head() {
    return {
      title: "Not Found",
    };
  },
};
</script>
