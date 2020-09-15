import {MetricDistance} from '@models/dependencyTree/metric.distance';

export function buildTooltipContent(
  libraryName: string, micAtDistance: MetricDistance,
  acAtDistance: MetricDistance, reachableClasses: number, reachableMethods: number, level: number): string {
  if (level === 0) {
    return '';
  }
  const reachableClassesText: string = isNaN(reachableClasses) ? 'N/A' : `${reachableClasses}%`;
  const reachableMethodsText: string = isNaN(reachableMethods) ? 'N/A' : `${reachableMethods}%`;

  let content = `<p><b>${libraryName}</b></p>`;
  content += '<br>';

  content += `<p>Reachable classes:  <b>${reachableClassesText}</b></p>`;
  content += `<p>Reachable methods:  <b>${reachableMethodsText}</b></p>`;
  content += '<br>';

  content += micAtDistance.toHTML();
  content += '<br>';
  content += acAtDistance.toHTML();

  return content;
}
