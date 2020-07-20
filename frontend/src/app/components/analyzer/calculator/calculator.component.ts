import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'calculator',
  templateUrl: './calculator.component.html',
  styleUrls: ['./calculator.component.css']
})
export class CalculatorComponent implements OnInit {

  title = 'Formula to calculate degree of dependency';
  exampleText = `\\begin{equation*}
                    \\sum_{\\verb|distance|} \\verb|MIC|(\\verb|distance|) / (\\verb|distance|\\times
                  \\end{equation*}`;
  constructor() {
  }

  ngOnInit(): void {
  }

}
