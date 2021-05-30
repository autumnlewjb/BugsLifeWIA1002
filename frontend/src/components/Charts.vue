<template>
  <v-container>
    <v-row>
      <v-container>
        <div class="container1">
          <highcharts
            id="container1"
            style="width: 50%; height: 400px; margin-bottom: 25px; float: left"
            :options="getChartOneOptions"
          ></highcharts>
        </div>
        <div class="container2">
          <highcharts
            id="container2"
            style="width: 50%; height: 400px; margin-bottom: 25px; float: right"
            :options="getChartTwoOptions"
          ></highcharts>
        </div>
        <div class="container3">
          <highcharts
            id="container3"
            style="width: 100%; height: 400px; margin-bottom: 25px; float: right"
            :options="getChartThreeOptions"
          ></highcharts>
        </div>
      </v-container>
    </v-row>
    <v-row>
      <v-container>
        <v-data-table :headers="getTableHeaders" :items="getTableData" class="elevation-1"></v-data-table>
      </v-container>
    </v-row>
  </v-container>
</template>

<script>
import {Chart} from 'highcharts-vue';
import Highcharts from 'highcharts';
import exportingInit from 'highcharts/modules/exporting'

exportingInit(Highcharts);

export default {
  name: "Charts",
  components: {
    highcharts: Chart
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
            data: [1, 2, 3]
          }
        ]
      }
    }
  },
  created() {
    fetch(`/api/${this.projectId}/charts/data`)
    .then((res) => res.json())
    .then(data => {
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
    .catch(e => console.log(e));
  },
  props: {
    projectId: String
  },
  computed: {
    getChartOneOptions() {
      return {
        chart: {
          type: 'column'
        },
        title: {
          text: 'Tags Bar Chart'
        },
        xAxis: {
          categories: this.tags,
          crosshair: true
        },
        yAxis: {
            min: 0,
            max: 20,
            title: {
                text: 'Counter'
            }
        },
        tooltip: {
            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                '<td style="padding:0"><b>{point.y}</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
        },
        plotOptions: {
            column: {
                pointPadding: 0.2,
                borderWidth: 0
            }
        },
        series: [{
            name: 'Tags',
            data: this.counter
        }]
      }
    },
    getChartTwoOptions() {
      return {
          chart: {
              plotBackgroundColor: null,
              plotBorderWidth: null,
              plotShadow: false,
              type: 'pie'
          },
          title: {
              text: 'Status Pie Chart'
          },
          tooltip: {
              pointFormat: '{series.name}: <b>{point.y}</b>'
          },
          accessibility: {
              point: {
                  valueSuffix: '%'
              }
          },
          plotOptions: {
              pie: {
                  allowPointSelect: true,
                  cursor: 'pointer',
                  dataLabels: {
                      enabled: true,
                      format: '<b>{point.name}</b>: {point.percentage:.1f} %'
                  }
              }
          },
          series: [{
              name: 'Status counter',
              colorByPoint: true,
              data: this.data
          }]
      }
    },
    getChartThreeOptions() {
      return {
            chart: {
                zoomType: 'xy'
            },
            title: {
                text: 'Number of issues created '
            },
            xAxis: [{
                categories: this.days,
                crosshair: true
            }],
            yAxis: [{ // Primary yAxis
                labels: {
                    format: '{value} total issues',
                    style: {
                        color: Highcharts.getOptions().colors[1]
                    }
                },
                title: {
                    text: 'Total issues',
                    style: {
                        color: Highcharts.getOptions().colors[1]
                    }
                }
            }, { // Secondary yAxis
                title: {
                    text: 'issues',
                    style: {
                        color: Highcharts.getOptions().colors[0]
                    }
                },
                labels: {
                    format: '{value} issues',
                    style: {
                        color: Highcharts.getOptions().colors[0]
                    }
                },
                opposite: true
            }],
            tooltip: {
                shared: true
            },
            legend: {
                layout: 'vertical',
                align: 'left',
                x: 120,
                verticalAlign: 'top',
                y: 100,
                floating: true,
                backgroundColor:
                    Highcharts.defaultOptions.legend.backgroundColor || // theme
                    'rgba(255,255,255,0.25)'
            },
            series: [{
                name: 'Issues',
                type: 'column',
                yAxis: 1,
                data: this.issueCounter,
                tooltip: {
                    valueSuffix: ' issues'
                }

            }, {
                name: 'Total issues',
                type: 'spline',
                data: this.cummulativeCounter,
                tooltip: {
                    valueSuffix: ' total issues'
                }
            }]

        }
    },
    getTableHeaders() {
      const headers = [];
      this.headers.forEach(element => {
        headers.push({
          text: element,
          value: element
        })
      });
      console.log(headers);
      return headers;
    },
    getTableData() {
      const rows = [];
      this.rows.forEach((element) => {
        rows.push(element);
      })
      console.log(rows);
      return rows;
    }
  }
};
</script>