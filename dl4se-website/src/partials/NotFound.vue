<script>
export default {
  name: "not-found",
  functional: true,
  render() {
    return (
      <div id="not-found">
        <h1 class="d-none">Not Found</h1>
        <b-container>
          <b-row>
            <b-col>
              <h2 class="text-center">Page Not Found</h2>
              <p class="text-center">
                The requested webpage can not be found, as it does not exist, or has been moved.
              </p>
            </b-col>
          </b-row>
        </b-container>
      </div>
    );
  },
};
</script>
