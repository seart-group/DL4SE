<template>
  <nav>
    <router-link to="/">Home</router-link> |
    <router-link to="/about">About</router-link>
  </nav>
  <router-view/>
</template>

<style>
@font-face {
  font-family: 'Trebuchet MS';
  font-style: normal;
  font-weight: normal;
  src: local('Trebuchet MS'), url('./assets/fonts/trebuc.woff') format('woff');
}

#app {
  font-family: 'Trebuchet MS', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: black;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}
</style>
