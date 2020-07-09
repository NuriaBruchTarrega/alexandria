import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'analyzer',
  templateUrl: './analyzer.component.html',
  styleUrls: ['./analyzer.component.css']
})
export class AnalyzerComponent implements OnInit {
  title = 'Specify client library';

  constructor() {
  }

  ngOnInit(): void {
  }

}
