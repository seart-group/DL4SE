const { defineConfig } = require("@vue/cli-service")
module.exports = defineConfig({
  transpileDependencies: true,
  pages: {
    index: {
      entry: "src/main.js",
      template: "public/index.html",
      filename: "index.html",
      title: "DL4SE",
      chunks: ["chunk-vendors", "chunk-common", "index"]
    }
  },
  devServer: {
    port: process.env.VUE_APP_PORT
  }
})
