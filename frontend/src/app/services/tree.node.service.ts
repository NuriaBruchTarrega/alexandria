import {Injectable} from '@angular/core';
import {Metrics} from '../enumerations/metrics';
import {DependencyTree} from '../models/dependencyTree/tree';
import {TreeNode} from '../models/dependencyTree/node';
import {ClassDistribution} from '../models/dependencyTree/class.distribution';
import {ChartData, ChartDataFactory} from '../models/chart.data';

@Injectable({
  providedIn: 'root'
})
export class TreeNodeService {

  constructor() {
  }

  calculateMetric(dependencyTree: DependencyTree, metric: Metrics, factor: number) {
    if (metric === Metrics.Tmic) {
      dependencyTree.calculateTMIC(factor);
    } else if (metric === Metrics.Tac) {
      dependencyTree.calculateTAC(factor);
    }
  }

  generateChartData(node: TreeNode): ChartData[] {
    const micClassDistribution: ClassDistribution = node.micClassDistribution;
    const acClassDistribution: ClassDistribution = node.acClassDistribution;

    const chartDataArray: ChartData[] = [];
    const classNames: Set<string> = new Set([...micClassDistribution.getAllClassNames(), ...acClassDistribution.getAllClassNames()]);

    classNames.forEach(className => {
      const micValue = micClassDistribution.getValue(className);
      const acValue = acClassDistribution.getValue(className);
      chartDataArray.push(ChartDataFactory.create(className, micValue, acValue));
    });

    return chartDataArray;
  }
}
