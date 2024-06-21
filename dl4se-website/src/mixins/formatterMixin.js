import startCase from "lodash-es/startCase";

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
    formatObjectAsTextList(item) {
      return Object.entries(item)
        .filter(([, value]) => {
          switch (typeof value) {
            case "string":
            case "object":
              return value?.length;
            default:
              return Boolean(value);
          }
        })
        .sort(([key1, value1], [key2, value2]) => {
          const order = ["boolean", "number", "string", "object"];
          const type1 = order.indexOf(typeof value1);
          const type2 = order.indexOf(typeof value2);
          if (type1 < type2) return 1;
          if (type1 > type2) return -1;
          else return key2.localeCompare(key1);
        })
        .map(([key, value]) => {
          const label = this.startCase(key);
          switch (typeof value) {
            case "boolean":
              return `- ${label}`;
            case "object":
              if (value instanceof Array) {
                const array = value.map((v) => `  - ${v}`).join("\n");
                return `- ${label}:\n${array}`;
              } else {
                const object = this.formatObjectAsTextList(value);
                const indented = object
                  .split("\n")
                  .map((line) => `  ${line}`)
                  .join("\n");
                return `- ${label}:\n${indented}`;
              }
            default:
              return `- ${label}: ${value}`;
          }
        })
        .join("\n");
    },
    startCase(value) {
      return startCase(value?.toLowerCase() ?? "???");
    },
  },
};
