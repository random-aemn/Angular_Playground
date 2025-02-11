import {AfterViewChecked, AfterViewInit, Component} from '@angular/core';


import * as Highcharts from "highcharts";
window.Highcharts = Highcharts;

// Turn on the high-chart context menu view/print/download options
import HC_exporting from "highcharts/modules/exporting";
HC_exporting(Highcharts);

// Turn on the high-chart context menu *export* options
// NOTE:  This provides these menu options: Download CSV, Download XLS, View Data Table
import HC_exportData from "highcharts/modules/export-data";
HC_exportData(Highcharts);

// Do client-side exporting (so that the exporting does *NOT* go to https://export.highcharts.com/
// NOTE:  This does not work on all web browsers
import HC_offlineExport from "highcharts/modules/offline-exporting";
HC_offlineExport(Highcharts);

// Turn on the drill-down capabilities
import HC_drillDown from "highcharts/modules/drilldown";
import {Chart} from "highcharts";

HC_drillDown(Highcharts);


@Component({
  selector: 'app-pie-chart-small-component',
  templateUrl: './pie-chart-small-component.component.html',
  styleUrls: ['./pie-chart-small-component.component.scss']
})
export class PieChartSmallComponentComponent implements AfterViewInit{

  private chartOptions: any =  {
    chart: {
      type: "pie",
    },
    title: {
      text: "Egg Yolk Composition",
    },
    tooltip: {
      valueSuffix: "%",
    },
    subtitle: {
      text: 'Source:<a href="https://www.mdpi.com/2072-6643/11/3/684/htm" target="_default">MDPI</a>',
    },
    plotOptions: {
      series: {
        allowPointSelect: true,
        cursor: "pointer",
        dataLabels: [
          {
            enabled: true,
            distance: 20,
          },
          {
            enabled: true,
            distance: -40,
            format: "{point.percentage:.1f}%",
            style: {
              fontSize: "1.2em",
              textOutline: "none",
              opacity: 0.7,
            },
            filter: {
              operator: ">",
              property: "percentage",
              value: 10,
            },
          },
        ],
      },
    },
    series: [
      {
        name: "Percentage",
        colorByPoint: true,
        data: [],
      },
    ],
  };

  private reloadData(): void{
    // This method set the series[0].data on the chartOptions

    // Update chart 1 with hard-coded data
    this.chartOptions.series[0].data = [
      {
        name: "Water",
        y: 55.02,
      },
      {
        name: "Fat",
        sliced: true,
        selected: true,
        y: 26.71,
      },
      {
        name: "Carbohydrates",
        y: 1.09,
      },
      {
        name: "Protein",
        y: 15.5,
      },
      {
        name: "Ash",
        y: 1.68,
      },
    ];

    // This renders the chart
    // NOTE:  You cannot render a chart from ngOnInit().  You can from ngAfterViewInit().
    Highcharts.chart('pie-chart1', this.chartOptions);

    // Redraw all of the charts on this page (so they fit perfectly within the mat-card tags
    Highcharts.charts.forEach(function (chart: Chart | undefined) {
      chart?.reflow();
    });

  }

  public ngAfterViewInit(){


    setTimeout(()=> {
      this.reloadData()
    })
    // this.reloadData();
  }


}
