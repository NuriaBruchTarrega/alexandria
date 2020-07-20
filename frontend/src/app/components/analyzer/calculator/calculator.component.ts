import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'calculator',
  templateUrl: './calculator.component.html',
  styleUrls: ['./calculator.component.css']
})
export class CalculatorComponent implements OnInit {

  title = 'Formula to calculate degree of dependency';
  exampleText = `sum_(distance)`;
  constructor() {
  }

  ngOnInit(): void {
  }

}
