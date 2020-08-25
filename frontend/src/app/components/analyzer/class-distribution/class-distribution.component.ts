import {Component, OnInit} from '@angular/core';
import {ChartData} from '@models/chart.data';
import {BLUE, GREY, PINK} from '@src/colors';

@Component({
  selector: 'class-distribution',
  templateUrl: './class-distribution.component.html',
  styleUrls: ['./class-distribution.component.css']
})
export class ClassDistributionComponent implements OnInit {
  classDistributionData: ChartData[] = [];

  // Chart options
  xAxisLabel = 'Classes';
  legendTitle = 'Metrics';
  yAxisLabel = 'Metrics value';
  legend = true;
  showXAxisLabel = true;
  showYAxisLabel = true;
  xAxis = true;
  yAxis = true;
  gradient = false;
  colorScheme = {
    domain: [PINK, BLUE, GREY]
  };
  animations = false;

  constructor() {
  }

  ngOnInit(): void {
  }

  updateClassDistributionData(data: ChartData[]) {
    this.classDistributionData = data;
  }

  axisFormat(val: number) {
    if (val % 1 === 0) {
      return val.toLocaleString();
    } else {
      return '';
    }
  }

}
