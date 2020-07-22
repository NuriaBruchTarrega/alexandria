export class ChartDataFactory {
  static create(completeClassName: string, micValue: number, acValue: number): ChartData {
    const classData: ClassData[] = [
      new ClassData('MIC', micValue, completeClassName),
      new ClassData('AC', acValue, completeClassName)
    ];
    const classPath: string[] = completeClassName.split('.');
    const className = classPath[classPath.length - 1];

    return new ChartData(className, classData);
  }
}

export class ChartData {
  name: string;
  series: ClassData[];

  constructor(name: string, series: ClassData[]) {
    this.name = name;
    this.series = series;
  }
}

export class ClassData {
  name: string;
  value: number;
  tooltip: string;

  constructor(name: string, value: number, tooltip: string) {
    this.name = name;
    this.value = value;
    this.tooltip = tooltip;
  }
}
