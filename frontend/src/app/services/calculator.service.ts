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
      dependencyTree.calculateTMIC(factor);
    } else if (metric === Metrics.Tac) {
      dependencyTree.calculateTAC(factor);
    }
  }
}
