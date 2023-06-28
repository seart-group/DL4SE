module.exports = {
  root: true,
  env: {
    node: true
  },
  extends: ["plugin:vue/essential", "eslint:recommended", "prettier"],
  parserOptions: {
    parser: "@babel/eslint-parser"
  },
  rules: {
    "no-console": process.env.NODE_ENV === "production" ? "warn" : "off",
    "no-debugger": process.env.NODE_ENV === "production" ? "warn" : "off",
    "no-unused-vars": process.env.NODE_ENV === "production" ? "warn" : "off",
    "vue/no-unused-vars": process.env.NODE_ENV === "production" ? "warn" : "off",
    "vue/no-unused-components": process.env.NODE_ENV === "production" ? "warn" : "off",
    "vue/multi-word-component-names": "off"
  }
}
