import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'class-distribution',
  templateUrl: './class-distribution.component.html',
  styleUrls: ['./class-distribution.component.css']
})
export class ClassDistributionComponent implements OnInit {
  classDistributionData = [];

  // Chart options
  view = [1000, 400];
  xAxisLabel = 'Classes';
  legendTitle = 'Class names';
  yAxisLabel = 'Measured Coupling';
  legend = true;
  showXAxisLabel = true;
  showYAxisLabel = true;
  xAxis = true;
  yAxis = true;
  gradient = false;

  constructor() {
  }

  ngOnInit(): void {
  }

}
