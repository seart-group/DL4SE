<template>
  <b-carousel-slide>
    <template #img>
      <b-card no-body>
        <b-card-header />
        <b-card-body>
          <b-card-title>
            <slot name="title" />
          </b-card-title>
          <b-card-text>
            <slot name="content" />
          </b-card-text>
        </b-card-body>
        <b-card-footer />
      </b-card>
    </template>
  </b-carousel-slide>
</template>

<script>
export default {
  name: "b-text-carousel-slide",
};
</script>

<style scoped lang="sass" src="@/assets/styles/component/text-carousel-slide.sass" />
