import startCase from "lodash-es/startCase";

const xmlFormatting = `
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:strip-space elements="*"/>
  <xsl:template match="para[content-style][not(text())]">
    <xsl:value-of select="normalize-space(.)"/>
  </xsl:template>
  <xsl:template match="node()|@*">
    <xsl:copy><xsl:apply-templates select="node()|@*"/></xsl:copy>
  </xsl:template>
  <xsl:output omit-xml-declaration="no" indent="yes"/>
</xsl:stylesheet>
`;

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
    formatObjectAsJson(item) {
      return JSON.stringify(item, null, 2);
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
    formatXML(value) {
      const xml = new DOMParser().parseFromString(value, "application/xml");
      const xslt = new DOMParser().parseFromString(xmlFormatting, "application/xml");
      const processor = new XSLTProcessor();
      processor.importStylesheet(xslt);
      const document = processor.transformToDocument(xml);
      return new XMLSerializer().serializeToString(document);
    },
    startCase(value) {
      return startCase(value?.toLowerCase() ?? "???");
    },
  },
};
