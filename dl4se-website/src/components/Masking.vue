<template>
  <div class="masking">
    <label :for="id + '-counter'" class="m-0">
      Randomly mask&nbsp;
    </label>
    <p class="masking-pad" />
    <b-counter :id="id + '-counter'" ref="counter-1"
               class="py-2" counter-class="masking-counter-input"
               :min="1" :max="100" placeholder="%"
               v-model.number="local.masking.percentage"
    />
    <p class="m-0">
      &nbsp;of&nbsp;
    </p>
    <b-break md />
    <p class="m-0">
      tokens&nbsp;
    </p>
    <label :for="id + '-mask'" class="m-0">
      using the&nbsp;
    </label>
    <div class="py-2">
      <b-input :id="id + '-mask'" class="masking-token-input"
               placeholder="<MASK>"
               v-model="local.masking.token"
      />
    </div>
    <p class="m-0">
      &nbsp;token
    </p>
  </div>
</template>

<script>
import BBreak from "@/components/Break"
import BCounter from "@/components/Counter";

export default {
  name: "b-masking",
  components: { BBreak, BCounter },
  props: {
    id: String,
    value: Object
  },
  computed: {
    state() {
      return Object.values(this.$refs).map(ref => ref.state)
          .filter(x => x !== null)
          .reduce((acc, curr) => acc && curr, true)
    }
  },
  watch: {
    "local.masking": {
      deep: true,
      handler() {
        this.$emit("input", this.local.masking)
      }
    }
  },
  data() {
    return {
      local: {
        masking: this.value
      }
    }
  }
}
</script>