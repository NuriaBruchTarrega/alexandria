export class MetricDistanceFactory {
  static create(mapJson: {}): MetricDistance {
    const metricDistance = new MetricDistance();

    for (const distance in mapJson) {
      if (mapJson.hasOwnProperty(distance)) {
        metricDistance.addDistance(Number(distance), mapJson[distance]);
      }
    }

    return metricDistance;
  }
}

export class MetricDistance {
  private _distanceMap: Map<number, number>;

  constructor() {
    this._distanceMap = new Map<number, number>();
  }

  addDistance(distance: number, value: number) {
    this._distanceMap.set(distance, value);
  }

  get distanceMap(): Map<number, number> {
    return this._distanceMap;
  }

  calculateMetric(factor: number): number {
    let result = 0;

    for (const [distance, value] of this._distanceMap) {
      result += value / (distance * factor);
    }

    return result;
  }
}
