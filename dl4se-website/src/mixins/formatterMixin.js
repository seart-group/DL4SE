export default {
  methods: {
    format(value, decimals = 2, k = 1000, units = ["", "K", "M", "B", "T"]) {
      if (!value) return `0${decimals ? "." : ""}${Array(decimals).fill(0).join("")} ${units[0]}`;
      const formatter = new Intl.NumberFormat("en-US", {
        minimumFractionDigits: decimals,
        maximumFractionDigits: decimals,
        roundingIncrement: 1,
        useGrouping: false,
      });
      const i = Math.floor(Math.log(value) / Math.log(k));
      const f = formatter.format(value / Math.pow(k, i));
      return `${f} ${units[i]}`;
    },
    formatNumber(value) {
      return this.format(value, 0);
    },
    formatNatural(value) {
      return this.format(value, 1, 1000, ["", "thousand", "million", "billion", "trillion"]);
    },
    formatBytes(value) {
      return this.format(value, 2, 1024, ["B", "KB", "MB", "GB", "TB", "PB"]);
    },
  },
};
