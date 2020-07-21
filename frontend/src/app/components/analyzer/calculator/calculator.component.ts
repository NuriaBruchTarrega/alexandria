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
  tacX: number;
  tmicX: number;

  constructor() {
  }

  ngOnInit(): void {
    this.tacX = 1;
    this.tmicX = 1;
    this.buildMicFormula('x');
    this.buildAcFormula('x');
  }

  tacFactorChange() {
    // Emit event
  }

  tmicFactorChange() {
    // Emit event
  }

  private buildMicFormula(factor) {
    this.micFormula = `\\begin{equation*}
                         \\verb|TMIC| = \\sum_{\\verb|distance|}
                         \\frac{\\verb|MIC|(\\verb|distance|)}{\\verb|distance| \\times ${factor}}
                        \\end{equation*}`;
  }

  private buildAcFormula(factor) {
    this.acFormula = `\\begin{equation*}
                        \\verb|TAC| = \\sum_{\\verb|distance|}
                        \\frac{\\verb|AC|(\\verb|distance|)}{\\verb|distance| \\times ${factor}}
                      \\end{equation*}`;
  }

}
