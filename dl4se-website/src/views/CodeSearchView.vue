<template>
  <div id="code-search">
    <h1 class="text-center">Search for Code</h1>
    <b-container>
      <b-row>
        <b-col>
          <b-form @submit.prevent.stop="submit">
            <b-form-row>
              <b-form-group label="Instance ID:">
                <b-form-input id="search" name="search" type="number" v-model="search" autofocus />
              </b-form-group>
            </b-form-row>
            <b-form-row>
              <b-form-group>
                <b-form-submit :disabled="v$.$invalid" />
              </b-form-group>
            </b-form-row>
          </b-form>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import useVuelidate from "@vuelidate/core";
import { minValue, required } from "@vuelidate/validators";
import BFormSubmit from "@/components/FormSubmit.vue";

export default {
  components: { BFormSubmit },
  methods: {
    submit() {
      this.$router.push({ name: "instance", params: { id: this.search } });
    },
  },
  setup() {
    return {
      v$: useVuelidate(),
    };
  },
  data() {
    return {
      search: null,
    };
  },
  validations() {
    return {
      search: {
        $autoDirty: true,
        required: required,
        positive: minValue(1),
      },
    };
  },
  head() {
    return {
      title: "Search",
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/view/code-search-view.sass" />
