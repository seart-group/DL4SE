<template>
  <div class="bar-chart">
    <h5 class="bar-chart-title" v-if="title">{{ title }}</h5>
    <b-overlay :show="busy" variant="light">
      <Bar :chart-id="id"
           :chart-data="chartData"
           :chart-options="chartOptions"
      />
    </b-overlay>
  </div>
</template>

<script>
import formatterMixin from "@/mixins/formatterMixin"
import {Bar} from "vue-chartjs/legacy"
import {BarElement, CategoryScale, Chart, Legend, LinearScale, Title, Tooltip} from "chart.js"

Chart.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

// https://nagix.github.io/chartjs-plugin-colorschemes/colorchart.html#:~:text=tableau.Brown20%3A-,tableau.Gray20,-%3A
const colors = [
    '#d5d5d5', '#cdcecd', '#c5c7c6', '#bcbfbe',
    '#b4b7b7', '#acb0b1', '#a4a9ab', '#9ca3a4',
    '#939c9e', '#8b9598', '#848e93', '#7c878d',
    '#758087', '#6e7a81', '#67737c', '#616c77',
    '#5b6570', '#555f6a', '#4f5864', '#49525e'
]

export default {
  name: "b-bar-chart",
  components: { Bar },
  mixins: [ formatterMixin ],
  props: {
    id: {
      type: String,
      default: ""
    },
    title: {
      type: String
    },
    supplier: {
      type: Function,
      default() {
        return {}
      }
    },
    label: {
      type: String,
      default: ""
    }
  },
  async mounted() {
    this.busy = true
    await this.supplier()
        .then(res => {
          const labels = Object.keys(res)
          const values = Object.values(res)

          this.chartData.labels = labels
          this.chartData.datasets = [
            {
              label: this.label,
              backgroundColor: colors,
              data: values
            }
          ]
        })
        .catch(() => {})
    this.busy = false
  },
  data() {
    return {
      busy: false,
      chartData: {
        labels: [],
        datasets: []
      },
      chartOptions: {
        responsive: true,
        scales: {
          y: {
            ticks: {
              callback: this.formatNumber
            }
          }
        },
        plugins: {
          tooltip: {
            yAlign: 'bottom',
            titleAlign: 'center',
            backgroundColor: '#000000',
            cornerRadius: 0
          },
          legend: {
            display: !!this.label
          }
        }
      }
    }
  }
}
</script>