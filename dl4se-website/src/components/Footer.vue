<template>
  <b-container fluid class="footer-container">
    <b-row align-h="between">
      <b-col md="auto" sm="12">
        <div class="footer-text">Created by:&nbsp;</div>
        <b-link href="https://github.com/dabico" target="_blank" class="footer-link">
          Ozren Dabić
        </b-link>
        <div class="footer-link-separator">,&nbsp;</div>
        <b-link href="https://emadpres.github.io" target="_blank" class="footer-link">
          Emad Aghajani
        </b-link>
        <div class="footer-link-separator">,&nbsp;</div>
        <b-link href="https://inf.usi.ch/faculty/bavota" target="_blank" class="footer-link">
          Gabriele Bavota
        </b-link>
      </b-col>
      <b-col md="auto" sm="12">
        <div class="footer-text">Maintained by:&nbsp;</div>
        <b-link href="https://seart.si.usi.ch" target="_blank" class="footer-link">SEART</b-link>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
export default {
  name: "b-footer"
}
</script>