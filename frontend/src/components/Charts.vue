<template>
  <v-container class="pa-2">
    <v-layout row class="py-10" justify-space-around>
      <v-flex md6 sm12>
        <v-card class="mr-1">
          <v-card-text>
            <highcharts :options="getChartOneOptions" class="pa-2"></highcharts>
          </v-card-text>
        </v-card>
      </v-flex>
      <v-flex md6 sm12>
        <v-card class="ml-1">
          <v-card-text>
            <highcharts :options="getChartTwoOptions" class="pa-2"></highcharts>
          </v-card-text>
        </v-card>
      </v-flex>
    </v-layout>
    <v-layout row>
      <v-flex>
        <v-card>
          <v-card-text>
            <highcharts :options="getChartThreeOptions"></highcharts>
          </v-card-text>
        </v-card>
      </v-flex>
    </v-layout>
    <v-layout row class="py-10">
      <v-flex>
        <v-container>
          <v-data-table
            :headers="getTableHeaders"
            :items="getTableData"
            class="elevation-1"
          ></v-data-table>
        </v-container>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import { Chart } from "highcharts-vue";
import Highcharts from "highcharts";
import exportingInit from "highcharts/modules/exporting";

exportingInit(Highcharts);

export default {
  name: "Charts",
  components: {
    highcharts: Chart,
  },
  data() {
    return {
      tags: [],
      cummulativeCounter: [],
      issueCounter: [],
      days: 0,
      counter: [],
      data: [],
      headers: [],
      rows: [],
      chartOptions: {
        series: [
          {
            data: [1, 2, 3],
          },
        ],
      },
    };
  },
  created() {
    fetch(`/api/${this.projectId}/charts/data`)
      .then((res) => res.json())
      .then((data) => {
        this.tags = data.tags;
        this.cummulativeCounter = data.cumulativeCounter;
        this.issueCounter = data.issueCounter;
        this.days = data.days;
        this.tags = data.tags;
        this.counter = data.counter;
        this.data = data.data;
        this.headers = data.headers;
        this.rows = data.rows;
      })
      .catch((e) => console.log(e));
  },
  props: {
    projectId: String,
  },
  computed: {
    getChartOneOptions() {
      return {
        chart: {
          type: "column",
        },
        title: {
          text: "Tags Bar Chart",
        },
        xAxis: {
          categories: this.tags,
          crosshair: true,
        },
        yAxis: {
          min: 0,
          max: 20,
          title: {
            text: "Counter",
          },
        },
        tooltip: {
          headerFormat:
            '<span style="font-size:10px">{point.key}</span><table>',
          pointFormat:
            '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
            '<td style="padding:0"><b>{point.y}</b></td></tr>',
          footerFormat: "</table>",
          shared: true,
          useHTML: true,
        },
        plotOptions: {
          column: {
            pointPadding: 0.2,
            borderWidth: 0,
          },
        },
        series: [
          {
            name: "Tags",
            data: this.counter,
          },
        ],
      };
    },
    getChartTwoOptions() {
      return {
        chart: {
          plotBackgroundColor: null,
          plotBorderWidth: null,
          plotShadow: false,
          type: "pie",
        },
        title: {
          text: "Status Pie Chart",
        },
        tooltip: {
          pointFormat: "{series.name}: <b>{point.y}</b>",
        },
        accessibility: {
          point: {
            valueSuffix: "%",
          },
        },
        plotOptions: {
          pie: {
            allowPointSelect: true,
            cursor: "pointer",
            dataLabels: {
              enabled: true,
              format: "<b>{point.name}</b>: {point.percentage:.1f} %",
            },
          },
        },
        series: [
          {
            name: "Status counter",
            colorByPoint: true,
            data: this.data,
          },
        ],
      };
    },
    getChartThreeOptions() {
      return {
        chart: {
          zoomType: "xy",
        },
        title: {
          text: "Number of issues created ",
        },
        xAxis: [
          {
            categories: this.days,
            crosshair: true,
          },
        ],
        yAxis: [
          {
            // Primary yAxis
            labels: {
              format: "{value} total issues",
              style: {
                color: Highcharts.getOptions().colors[1],
              },
            },
            title: {
              text: "Total issues",
              style: {
                color: Highcharts.getOptions().colors[1],
              },
            },
          },
          {
            // Secondary yAxis
            title: {
              text: "issues",
              style: {
                color: Highcharts.getOptions().colors[0],
              },
            },
            labels: {
              format: "{value} issues",
              style: {
                color: Highcharts.getOptions().colors[0],
              },
            },
            opposite: true,
          },
        ],
        tooltip: {
          shared: true,
        },
        legend: {
          layout: "vertical",
          align: "left",
          x: 120,
          verticalAlign: "top",
          y: 100,
          floating: true,
          backgroundColor:
            Highcharts.defaultOptions.legend.backgroundColor || // theme
            "rgba(255,255,255,0.25)",
        },
        series: [
          {
            name: "Issues",
            type: "column",
            yAxis: 1,
            data: this.issueCounter,
            tooltip: {
              valueSuffix: " issues",
            },
          },
          {
            name: "Total issues",
            type: "spline",
            data: this.cummulativeCounter,
            tooltip: {
              valueSuffix: " total issues",
            },
          },
        ],
      };
    },
    getTableHeaders() {
      const headers = [];
      this.headers.forEach((element) => {
        headers.push({
          text: element,
          value: element,
        });
      });
      console.log(headers);
      return headers;
    },
    getTableData() {
      const rows = [];
      this.rows.forEach((element) => {
        rows.push(element);
      });
      console.log(rows);
      return rows;
    },
  },
};
</script>

<style scoped>

</style>