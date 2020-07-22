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

  getAllClassNames() {
    const classNames: Set<string> = new Set<string>();
    this._classDistributionMap.forEach((_, key) => classNames.add(key));
    return classNames;
  }

  getValue(className: string): number {
    return this._classDistributionMap.get(className) || 0;
  }
}
