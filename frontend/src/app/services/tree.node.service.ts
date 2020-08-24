import {Injectable} from '@angular/core';
import {Metrics} from '@enumerations/metrics';
import {DependencyTree} from '@models/dependencyTree/tree';
import {TreeNode} from '@models/dependencyTree/node';
import {ClassDistribution} from '@models/dependencyTree/class.distribution';
import {ChartData, ChartDataFactory} from '@models/chart.data';

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
    const callsDistribution: ClassDistribution = node.callsDistribution;
    const fieldsDistribution: ClassDistribution = node.fieldsDistribution;

    const chartDataArray: ChartData[] = [];
    const classNames: Set<string> = new Set([...callsDistribution.getAllClassNames(), ...fieldsDistribution.getAllClassNames()]);

    classNames.forEach(className => {
      const callsValue = callsDistribution.getValue(className);
      const fieldsValue = fieldsDistribution.getValue(className);
      chartDataArray.push(ChartDataFactory.create(className, callsValue, fieldsValue));
    });

    return chartDataArray;
  }
}
