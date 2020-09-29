export class BenchmarkResult {
  directDependencies: DirectDependenciesData[];
  transitiveDependenciesMic: TransitiveDependenciesData[];
  transitiveDependenciesAc: TransitiveDependenciesData[];

  constructor() {
    this.directDependencies = [];
    this.transitiveDependenciesMic = [];
    this.transitiveDependenciesAc = [];
  }

  addDirectDependencyData(directDependencyData: DirectDependenciesData) {
    this.directDependencies.push(directDependencyData);
  }

  addTransitiveDependencyMicData(transitiveDependencyMicData: TransitiveDependenciesData) {
    this.transitiveDependenciesMic.push(transitiveDependencyMicData);
  }

  addTransitiveDependencyAcData(transitiveDependencyAcData: TransitiveDependenciesData) {
    this.transitiveDependenciesAc.push(transitiveDependencyAcData);
  }
}

export class DirectDependenciesData {
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
