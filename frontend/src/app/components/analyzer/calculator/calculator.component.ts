import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {isNil} from 'lodash';

@Component({
  selector: 'calculator',
  templateUrl: './calculator.component.html',
  styleUrls: ['./calculator.component.css']
})
export class CalculatorComponent implements OnInit {
  @Output() changedTacFactor = new EventEmitter();
  @Output() changedTmicFactor = new EventEmitter();

  title = 'Formulas to calculate degree of dependency';
  formula: string;
  tacX: number;
  tmicX: number;

  constructor() {
  }

  ngOnInit(): void {
    this.tacX = 0.90;
    this.tmicX = 0.90;
    this.buildFormula('PF');
  }

  tmicFactorChange() {
    if (!isNil(this.tmicX)) {
      this.changedTmicFactor.emit(this.tmicX);
    }
  }

  tacFactorChange() {
    if (!isNil(this.tacX)) {
      this.changedTacFactor.emit(this.tacX);
    }
  }

  private buildFormula(factor) {
    this.formula = `\\begin{equation*}
                         \\verb|TMetric| = \\sum_{\\verb|distance|}
                         \\verb|Metric|(\\verb|distance|) \\times \\verb|${factor}|^\\verb|distance|
                        \\end{equation*}`;
  }

}
