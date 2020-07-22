import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'class-distribution',
  templateUrl: './class-distribution.component.html',
  styleUrls: ['./class-distribution.component.css']
})
export class ClassDistributionComponent implements OnInit {
  classDistributionData = [];

  constructor() {
  }

  ngOnInit(): void {
  }

}
