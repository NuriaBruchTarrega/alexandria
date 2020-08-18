import {MetricDistance} from '@models/dependencyTree/metric.distance';

export function buildTooltipContent(
  libraryName: string, micAtDistance: MetricDistance,
  acAtDistance: MetricDistance, annAtDistance: MetricDistance): string {
  let test = `<p>${libraryName}</p>`;

  test += micAtDistance.toHTML();
  test += '<br>';
  test += acAtDistance.toHTML();
  test += '<br>';
  test += annAtDistance.toHTML();

  return test;
}
