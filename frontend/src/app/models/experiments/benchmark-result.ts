export class BenchmarkResultFactory {
  static create({
                  benchmarkMic = {directDependencies: [], transitiveDependencies: []},
                  benchmarkAc = {directDependencies: [], transitiveDependencies: []}
                }): BenchmarkResult {
    const directDependenciesData: DirectDependencyData[] =
      this.calculateDirectDependenciesBenchmark(benchmarkMic.directDependencies, benchmarkAc.directDependencies);
    const transitiveDependenciesMicData: TransitiveDependenciesData[] =
      this.calculateTransitiveDependenciesBenchmark(benchmarkMic.transitiveDependencies);
    const transitiveDependenciesAcData: TransitiveDependenciesData[] =
      this.calculateTransitiveDependenciesBenchmark(benchmarkAc.transitiveDependencies);

    return new BenchmarkResult(directDependenciesData, transitiveDependenciesMicData, transitiveDependenciesAcData);
  }

  private static calculateDirectDependenciesBenchmark(directDependenciesMic: any[], directDependenciesAc: any[]): DirectDependencyData[] {
    const directDependenciesData: DirectDependencyData[] = [];
    let index = 0;

    while (index < directDependenciesMic.length) {
      directDependenciesData.push(new DirectDependencyData(directDependenciesMic[index] || 0, directDependenciesAc[index] || 0));
      index++;
    }

    return directDependenciesData;
  }

  private static calculateTransitiveDependenciesBenchmark(transitiveDependenciesMetric: any[]) {
    const transitiveDependenciesData: TransitiveDependenciesData[] = [];
    let dependencyId = 0;

    transitiveDependenciesMetric.forEach(transitiveDependencyMetric => {
      for (const distance in transitiveDependencyMetric) {
        if (transitiveDependencyMetric.hasOwnProperty(distance)) {
          transitiveDependenciesData.push(
            new TransitiveDependenciesData(dependencyId, Number(distance), transitiveDependencyMetric[distance]));
        }
      }

      if (transitiveDependencyMetric.size === 0) {
        transitiveDependenciesData.push(new TransitiveDependenciesData(dependencyId, 0, 0));
      }

      ++dependencyId;
    });

    return transitiveDependenciesData;
  }
}

export class BenchmarkResult {
  directDependencies: DirectDependencyData[];
  transitiveDependenciesMic: TransitiveDependenciesData[];
  transitiveDependenciesAc: TransitiveDependenciesData[];

  constructor(
    directDependencies: DirectDependencyData[],
    transitiveDependenciesMic: TransitiveDependenciesData[],
    transitiveDependenciesAc: TransitiveDependenciesData[]) {
    this.directDependencies = directDependencies;
    this.transitiveDependenciesMic = transitiveDependenciesMic;
    this.transitiveDependenciesAc = transitiveDependenciesAc;
  }
}

export class DirectDependencyData {
  mic: number;
  ac: number;

  constructor(mic: number, ac: number) {
    this.mic = mic;
    this.ac = ac;
  }
}

export class TransitiveDependenciesData {
  id: number;
  distance: number;
  value: number;

  constructor(id: number, distance: number, value: number) {
    this.id = id;
    this.distance = distance;
    this.value = value;
  }
}
