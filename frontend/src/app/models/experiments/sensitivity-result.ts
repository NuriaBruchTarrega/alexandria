export class SensitivityResultFactory {
  static create({
                  clientLibrary = '', serverLibrary = '',
                  micSensitivityAnalysisData = {}, acSensitivityAnalysisData = {}
                }): SensitivityResult {
    const micSensitivityMap = this.createSensitivityMap(micSensitivityAnalysisData);
    const acSensitivityMap = this.createSensitivityMap(acSensitivityAnalysisData);

    return new SensitivityResult(clientLibrary, serverLibrary, micSensitivityMap, acSensitivityMap);
  }

  private static createSensitivityMap(sensitivityAnalysisData = {}): Map<number, number> {
    const map = new Map<number, number>();

    for (const factor in sensitivityAnalysisData) {
      if (sensitivityAnalysisData.hasOwnProperty(factor)) {
        map.set(Number(factor), sensitivityAnalysisData[factor]);
      }
    }

    return map;
  }
}

export class SensitivityResult {
  clientLibrary: string;
  serverLibrary: string;
  micSensitivityMap: Map<number, number>;
  acSensitivityMap: Map<number, number>;

  constructor(clientLibrary: string, serverLibrary: string, micSensitivityMap: Map<number, number>, acSensitivityMap: Map<number, number>) {
    this.clientLibrary = clientLibrary;
    this.serverLibrary = serverLibrary;
    this.micSensitivityMap = micSensitivityMap;
    this.acSensitivityMap = acSensitivityMap;
  }
}
