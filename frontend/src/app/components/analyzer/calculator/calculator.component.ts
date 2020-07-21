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
  constructor() {
  }

  ngOnInit(): void {
    this.buildMICFormula('x');
    this.buildACFormula('x');
  }

  private buildMICFormula(factor) {
    this.micFormula = `\\begin{equation*}
                         \\verb|TMIC| = \\sum_{\\verb|distance|} \\frac{\\verb|MIC|(\\verb|distance|)}{\\verb|distance| \\times ${factor}}
                        \\end{equation*}`;
  }

  private buildACFormula(factor) {
    this.acFormula = `\\begin{equation*}
                          \\verb|TAC| = \\sum_{\\verb|distance|} \\frac{\\verb|AC|(\\verb|distance|)}{\\verb|distance| \\times ${factor}}
                        \\end{equation*}`;
  }

}
