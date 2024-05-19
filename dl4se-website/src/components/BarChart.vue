<template>
  <b-overlay :show="busy" variant="light">
    <Bar :chart-id="id" :chart-data="chartData" :chart-options="chartOptions" class="chart bar-chart">
      <slot>Error rendering chart!</slot>
    </Bar>
  </b-overlay>
</template>

<script>
import formatterMixin from "@/mixins/formatterMixin";
import chroma from "chroma-js";
import { Bar } from "vue-chartjs/legacy";
import { BarElement, CategoryScale, Chart, Legend, LinearScale, Title, Tooltip } from "chart.js";

Chart.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale);
const scale = chroma.scale(["#343a40", "#adb5bd"]).mode("lch");

export default {
  name: "b-bar-chart",
  components: { Bar },
  mixins: [formatterMixin],
  props: {
    id: {
      type: String,
      default: "",
    },
    title: {
      type: String,
    },
    supplier: {
      type: Function,
      default() {
        return {};
      },
    },
    label: {
      type: String,
      default: "",
    },
  },
  async mounted() {
    this.busy = true;
    await this.supplier()
      .then((res) => {
        const labels = Object.keys(res);
        const values = Object.values(res);
        const palette = scale.colors(labels.length);

        this.chartData.labels = labels;
        this.chartData.datasets = [
          {
            label: this.label,
            borderColor: palette,
            borderWidth: 2,
            backgroundColor: palette.map((color) => `${color}bf`),
            barPercentage: 1,
            data: values,
          },
        ];
      })
      .catch(() => {});
    this.busy = false;
  },
  data() {
    return {
      busy: false,
      chartData: {
        labels: [],
        datasets: [],
      },
      chartOptions: {
        responsive: true,
        scales: {
          y: {
            ticks: {
              callback: this.formatNumber,
            },
          },
        },
        plugins: {
          title: {
            display: true,
            text: this.title,
            color: "#000000",
            font: {
              size: 16,
            },
          },
          tooltip: {
            yAlign: "bottom",
            titleAlign: "center",
            backgroundColor: "#000000",
            cornerRadius: 0,
          },
          legend: {
            display: !!this.label,
          },
        },
      },
    };
  },
};
</script>
