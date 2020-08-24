import {MetricDistance} from '@models/dependencyTree/metric.distance';

export function buildTooltipContent(
  libraryName: string, micAtDistance: MetricDistance,
  acAtDistance: MetricDistance, reachableClasses: number, reachableMethods: number): string {
  let content = `<p><b>${libraryName}</b></p>`;
  content += '<br>';

  content += `<p>Reachable classes:  <b>${reachableClasses}%</b></p>`;
  content += `<p>Reachable methods:  <b>${reachableMethods}%</b></p>`;
  content += '<br>';

  content += micAtDistance.toHTML();
  content += '<br>';
  content += acAtDistance.toHTML();

  return content;
}
