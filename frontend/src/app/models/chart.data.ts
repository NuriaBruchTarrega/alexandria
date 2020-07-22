export class ChartDataFactory {
  static create(className: string, micValue: number, acValue: number): ChartData {
    const classData: ClassData[] = [
      new ClassData('MIC', micValue),
      new ClassData('AC', acValue)
    ];
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

  constructor(name: string, value: number) {
    this.name = name;
    this.value = value;
  }
}
