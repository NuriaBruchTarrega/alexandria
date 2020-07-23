import {Component, OnInit} from '@angular/core';
import {ChartData} from '../../../models/chart.data';

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
    domain: ['#e91e63', '#3F51B5', '#AAAAAA']
  };
  animations = false;

  constructor() {
  }

  ngOnInit(): void {
  }

  updateClassDistributionData(data: ChartData[]) {
    this.classDistributionData = data;
  }

}