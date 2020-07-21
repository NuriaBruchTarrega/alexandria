import {Injectable} from '@angular/core';
import {Metrics} from '../enumerations/metrics';
import {DependencyTree} from '../models/dependencyTree/tree';

@Injectable({
  providedIn: 'root'
})
export class CalculatorService {

  constructor() {
  }

  calculateMetric(dependencyTree: DependencyTree, metric: Metrics, factor: number) {
    if (metric === Metrics.Tmic) {
      this.calculateTMIC(dependencyTree, factor);
    } else if (metric === Metrics.Tac) {
      this.calculateTAC(dependencyTree, factor);
    }
  }

  private calculateTMIC(dependencyTree: DependencyTree, factor: number) {
    // Calculate TMIC
  }

  private calculateTAC(dependencyTree: DependencyTree, factor: number) {
    // Calculate TAC
  }
}
