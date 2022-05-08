<template>
  <div id="app">
    <router-view />
  </div>
</template>

<style>
@font-face {
  font-family: 'Trebuchet MS';
  font-style: normal;
  font-weight: normal;
  src: local('Trebuchet MS'), url('./assets/fonts/trebuc.woff') format('woff');
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  min-height: 100vh;
}

.page-title {
  text-align: center;
  margin: 1.5rem;
}

#app {
  font-family: 'Trebuchet MS', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: black;
}

:disabled {
  cursor: not-allowed;
  pointer-events: all !important;
}
</style>
