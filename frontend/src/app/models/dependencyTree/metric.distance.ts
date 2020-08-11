
export class MetricDistanceFactory {
  static create(mapJson: {}, name: string): MetricDistance {
    const metricDistance = new MetricDistance(name);

    for (const distance in mapJson) {
      if (mapJson.hasOwnProperty(distance)) {
        metricDistance.addDistance(Number(distance), mapJson[distance]);
      }
    }

    return metricDistance;
  }
}

export class MetricDistance {
  private readonly _distanceMap: Map<number, number>;
  private readonly name: string;

  constructor(name) {
    this._distanceMap = new Map<number, number>();
    this.name = name;
  }

  addDistance(distance: number, value: number) {
    this._distanceMap.set(distance, value);
  }

  getValueAtDistance(distance: number): number {
    return this._distanceMap.get(distance);
  }

  calculateMetric(factor: number): number {
    let result = 0;

    for (const [distance, value] of this._distanceMap) {
      if (distance === 1) {
        result += value;
      } else {
        result += value * Math.pow(factor, distance);
      }
    }

    return result;
  }

  toHTML(): string {
    console.log(this._distanceMap);
    let test = `
      <table class="table">
      <thead>
      <tr>
      <th>${this.name}</th>
      <th>Distance</th>
      </tr>
      </thead>
      <tbody>
      `;

    this._distanceMap.forEach((value, distance, _) => {
      test += `
        <tr>
        <td>${this._distanceMap.get(distance)}</td>
        <td>${distance}</td>
        </tr>
        `;
    });

    test += '</tbody></table>';
    return test;
  }
}
