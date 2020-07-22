export class ClassDistributionFactory {
  static create(mapJson: {}): ClassDistribution {
    const classDistribution = new ClassDistribution();

    for (const className in mapJson) {
      if (mapJson.hasOwnProperty(className)) {
        classDistribution.addClass(className, mapJson[className]);
      }
    }

    return classDistribution;
  }
}

export class ClassDistribution {
  private _classDistributionMap: Map<string, number>;

  constructor() {
    this._classDistributionMap = new Map<string, number>();
  }

  addClass(className: string, value: number) {
    this._classDistributionMap.set(className, value);
  }
}
