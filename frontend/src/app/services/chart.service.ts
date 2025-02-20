import { Injectable } from '@angular/core';
import {delay, Observable, of} from "rxjs";
import {GetChart2DataDTO} from "../models/get-chart2-data-dto";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class ChartService {

  constructor(private httpClient: HttpClient) { }

  public darkUnicaTheme: any = {

      colors: [
        '#2b908f', '#90ee7e', '#f45b5b', '#7798BF', '#aaeeee', '#ff0066',
        '#eeaaee', '#55BF3B', '#DF5353', '#7798BF', '#aaeeee'
      ],
      chart: {
        backgroundColor: {
          linearGradient: { x1: 0, y1: 0, x2: 1, y2: 1 },
          stops: [
            [0, '#2a2a2b'],
            [1, '#3e3e40']
          ]
        },
        style: {
          fontFamily: '\'Unica One\', sans-serif'
        },
        plotBorderColor: '#606063'
      },
      title: {
        style: {
          color: '#E0E0E3',
          textTransform: 'uppercase',
          fontSize: '20px'
        }
      },
      subtitle: {
        style: {
          color: '#E0E0E3',
          textTransform: 'uppercase'
        }
      },
      xAxis: {
        gridLineColor: '#707073',
        labels: {
          style: {
            color: '#E0E0E3'
          }
        },
        lineColor: '#707073',
        minorGridLineColor: '#505053',
        tickColor: '#707073',
        title: {
          style: {
            color: '#A0A0A3'
          }
        }
      },
      yAxis: {
        gridLineColor: '#707073',
        labels: {
          style: {
            color: '#E0E0E3'
          }
        },
        lineColor: '#707073',
        minorGridLineColor: '#505053',
        tickColor: '#707073',
        tickWidth: 1,
        title: {
          style: {
            color: '#A0A0A3'
          }
        }
      },
      tooltip: {
        backgroundColor: 'rgba(0, 0, 0, 0.85)',
        style: {
          color: '#F0F0F0'
        }
      },
      plotOptions: {
        series: {
          dataLabels: {
            color: '#F0F0F3',
            style: {
              fontSize: '13px'
            }
          },
          marker: {
            lineColor: '#333'
          }
        },
        boxplot: {
          fillColor: '#505053'
        },
        candlestick: {
          lineColor: 'white'
        },
        errorbar: {
          color: 'white'
        }
      },
      legend: {
        backgroundColor: 'rgba(0, 0, 0, 0.5)',
        itemStyle: {
          color: '#E0E0E3'
        },
        itemHoverStyle: {
          color: '#FFF'
        },
        itemHiddenStyle: {
          color: '#606063'
        },
        title: {
          style: {
            color: '#C0C0C0'
          }
        }
      },
      credits: {
        style: {
          color: '#666'
        }
      },
      drilldown: {
        activeAxisLabelStyle: {
          color: '#F0F0F3'
        },
        activeDataLabelStyle: {
          color: '#F0F0F3'
        }
      },
      navigation: {
        buttonOptions: {
          symbolStroke: '#DDDDDD',
          theme: {
            fill: '#505053'
          }
        }
      },
      // scroll charts
      rangeSelector: {
        buttonTheme: {
          fill: '#505053',
          stroke: '#000000',
          style: {
            color: '#CCC'
          },
          states: {
            hover: {
              fill: '#707073',
              stroke: '#000000',
              style: {
                color: 'white'
              }
            },
            select: {
              fill: '#000003',
              stroke: '#000000',
              style: {
                color: 'white'
              }
            }
          }
        },
        inputBoxBorderColor: '#505053',
        inputStyle: {
          backgroundColor: '#333',
          color: 'silver'
        },
        labelStyle: {
          color: 'silver'
        }
      },
      navigator: {
        handles: {
          backgroundColor: '#666',
          borderColor: '#AAA'
        },
        outlineColor: '#CCC',
        maskFill: 'rgba(255,255,255,0.1)',
        series: {
          color: '#7798BF',
          lineColor: '#A6C7ED'
        },
        xAxis: {
          gridLineColor: '#505053'
        }
      },
      scrollbar: {
        barBackgroundColor: '#808083',
        barBorderColor: '#808083',
        buttonArrowColor: '#CCC',
        buttonBackgroundColor: '#606063',
        buttonBorderColor: '#606063',
        rifleColor: '#FFF',
        trackBackgroundColor: '#404043',
        trackBorderColor: '#404043'
      }
    // }
  };

  public textBright = '#F0F0F3';

  public highContrastDarkTheme: any = {
    colors: [
      '#67B9EE',
      '#CEEDA5',
      '#9F6AE1',
      '#FEA26E',
      '#6BA48F',
      '#EA3535',
      '#8D96B7',
      '#ECCA15',
      '#20AA09',
      '#E0C3E4'
    ],
    chart: {
      backgroundColor: '#1f1f20',
      plotBorderColor: '#606063'
    },
    title: {
      style: {
        color: this.textBright
      }
    },
    subtitle: {
      style: {
        color: this.textBright
      }
    },
    xAxis: {
      gridLineColor: '#707073',
      labels: {
        style: {
          color: this.textBright
        }
      },
      lineColor: '#707073',
      minorGridLineColor: '#505053',
      tickColor: '#707073',
      title: {
        style: {
          color: this.textBright
        }
      }
    },
    yAxis: {
      gridLineColor: '#707073',
      labels: {
        style: {
          color: this.textBright
        }
      },
      lineColor: '#707073',
      minorGridLineColor: '#505053',
      tickColor: '#707073',
      title: {
        style: {
          color: this.textBright
        }
      }
    },
    tooltip: {
      backgroundColor: 'rgba(0, 0, 0, 0.85)',
      style: {
        color: this.textBright
      }
    },
    plotOptions: {
      series: {
        dataLabels: {
          color: this.textBright
        },
        marker: {
          lineColor: '#333'
        }
      },
      boxplot: {
        fillColor: '#505053'
      },
      candlestick: {
        lineColor: 'white'
      },
      errorbar: {
        color: 'white'
      },
      map: {
        nullColor: '#353535'
      }
    },
    legend: {
      backgroundColor: 'transparent',
      itemStyle: {
        color: this.textBright
      },
      itemHoverStyle: {
        color: '#FFF'
      },
      itemHiddenStyle: {
        color: '#606063'
      },
      title: {
        style: {
          color: '#D0D0D0'
        }
      }
    },
    credits: {
      style: {
        color: this.textBright
      }
    },
    drilldown: {
      activeAxisLabelStyle: {
        color: this.textBright
      },
      activeDataLabelStyle: {
        color: this.textBright
      }
    },
    navigation: {
      buttonOptions: {
        symbolStroke: '#DDDDDD',
        theme: {
          fill: '#505053'
        }
      }
    },
    rangeSelector: {
      buttonTheme: {
        fill: '#505053',
        stroke: '#000000',
        style: {
          color: '#eee'
        },
        states: {
          hover: {
            fill: '#707073',
            stroke: '#000000',
            style: {
              color: this.textBright
            }
          },
          select: {
            fill: '#303030',
            stroke: '#101010',
            style: {
              color: this.textBright
            }
          }
        }
      },
      inputBoxBorderColor: '#505053',
      inputStyle: {
        backgroundColor: '#333',
        color: this.textBright
      },
      labelStyle: {
        color: this.textBright
      }
    },
    navigator: {
      handles: {
        backgroundColor: '#666',
        borderColor: '#AAA'
      },
      outlineColor: '#CCC',
      maskFill: 'rgba(180,180,255,0.2)',
      series: {
        color: '#7798BF',
        lineColor: '#A6C7ED'
      },
      xAxis: {
        gridLineColor: '#505053'
      }
    },
    scrollbar: {
      barBackgroundColor: '#808083',
      barBorderColor: '#808083',
      buttonArrowColor: '#CCC',
      buttonBackgroundColor: '#606063',
      buttonBorderColor: '#606063',
      rifleColor: '#FFF',
      trackBackgroundColor: '#404043',
      trackBorderColor: '#404043'
    }
  };

  public getAllDataForChart2(): Observable<GetChart2DataDTO[]> {

    let dto: GetChart2DataDTO[] = [
      {

          name: 'Installation & Developers',
          data: [
            43934, 48656, 65165, 81827, 112143, 142383,
            171533, 165174, 155157, 161454, 154610, 168960, 171558
          ]
        }, {
        name: 'Manufacturing',
        data: [
          24916, 37941, 29742, 29851, 32490, 30282,
          38121, 36885, 33726, 34243, 31050, 33099, 33473
        ]
      }, {
        name: 'Sales & Distribution',
        data: [
          11744, 30000, 16005, 19771, 20185, 24377,
          32147, 30912, 29243, 29213, 25663, 28978, 30618
        ]
      }, {
        name: 'Operations & Maintenance',
        data: [
          null, null, null, null, null, null, null,
          null, 11164, 11218, 10077, 12530, 16585
        ]
      }, {
        name: 'Other',
        data: [
          21908, 5548, 8105, 11248, 8989, 11816, 18274,
          17300, 13053, 11906, 10073, 11471, 11648
        ]


      }
    ]


    return of(dto).pipe(delay(5000));

  }

  public getUsaMapData(): Observable<any>{
    // URL to get the map data
    const restUrl: string = 'https://www.highcharts.com/samples/data/us-population-density.json';

    // Return an observable that will invoke this REST call
    return this.httpClient.get(restUrl);


  }


}
