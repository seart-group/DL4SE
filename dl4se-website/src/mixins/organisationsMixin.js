export default {
  computed: {
    organisationsURL() {
      return `${process.env.VUE_APP_API_BASE_URL}/organisation`;
    },
  },
};
