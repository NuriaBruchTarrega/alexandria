import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'calculator',
  templateUrl: './calculator.component.html',
  styleUrls: ['./calculator.component.css']
})
export class CalculatorComponent implements OnInit {

  title = 'Formula to calculate degree of dependency';
  micFormula: string;
  acFormula: string;
  acX = 1;
  micX = 1;

  constructor() {
  }

  ngOnInit(): void {
    this.buildMICFormula();
    this.buildACFormula();
  }

  private buildMICFormula() {
    this.micFormula = `\\begin{equation*}
                         \\verb|TMIC| = \\sum_{\\verb|distance|}
                         \\frac{\\verb|MIC|(\\verb|distance|)}{\\verb|distance| \\times ${this.micX}}
                        \\end{equation*}`;
  }

  private buildACFormula() {
    this.acFormula = `\\begin{equation*}
                        \\verb|TAC| = \\sum_{\\verb|distance|}
                        \\frac{\\verb|AC|(\\verb|distance|)}{\\verb|distance| \\times ${this.acX}}
                      \\end{equation*}`;
  }

}
