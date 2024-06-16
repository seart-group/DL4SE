const { defineConfig } = require("@vue/cli-service");
module.exports = defineConfig({
  lintOnSave: true,
  transpileDependencies: true,
  pages: {
    index: {
      entry: "src/main.js",
      template: "public/index.html",
      filename: "index.html",
      title: "Data Hub",
      chunks: ["chunk-vendors", "chunk-common", "index"],
    },
  },
  pwa: {
    name: "SEART Data Hub",
    themeColor: "#F8F9FA",
    manifestOptions: {
      short_name: "Data Hub",
      description:
        "Building Training Datasets for Deep Learning Models in Software Engineering and Empirical Software Engineering Research",
      categories: [
        "DL4SE",
        "deep learning",
        "software engineering",
        "deep learning for software engineering",
        "empirical software engineering",
        "mining software repositories",
        "mining source code",
        "dataset generation",
        "training datasets",
        "research",
      ],
      screenshots: [],
      start_url: "/",
      background_color: "#FFFFFF",
      display: "minimal-ui",
      orientation: "any",
      dir: "ltr",
      lang: "en",
      launch_handler: {
        client_mode: "auto",
      },
    },
  },
  devServer: {
    port: process.env.VUE_APP_PORT,
  },
});
