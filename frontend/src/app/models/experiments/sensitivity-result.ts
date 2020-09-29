export class SensitivityResultFactory {
  static create({
                  clientLibrary = '', serverLibrary = '',
                  micSensitivityAnalysisData = {}, acSensitivityAnalysisData = {},
                  micAtDistance = {}, acAtDistance = {}
                }): SensitivityResult {
    const micSensitivityMap = this.createMapFromJson(micSensitivityAnalysisData);
    const acSensitivityMap = this.createMapFromJson(acSensitivityAnalysisData);
    const micValuesMap = this.createMapFromJson(micAtDistance);
    const acValuesMap = this.createMapFromJson(acAtDistance);

    return new SensitivityResult(clientLibrary, serverLibrary, micSensitivityMap, acSensitivityMap, micValuesMap, acValuesMap);
  }

  private static createMapFromJson(sensitivityAnalysisData = {}): Map<number, number> {
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
  micValuesMap: Map<number, number>;
  acValuesMap: Map<number, number>;

  constructor(clientLibrary: string, serverLibrary: string,
              micSensitivityMap: Map<number, number>, acSensitivityMap: Map<number, number>,
              micValuesMap: Map<number, number>, acValuesMap: Map<number, number>) {
    this.clientLibrary = clientLibrary;
    this.serverLibrary = serverLibrary;
    this.micSensitivityMap = micSensitivityMap;
    this.acSensitivityMap = acSensitivityMap;
    this.micValuesMap = micValuesMap;
    this.acValuesMap = acValuesMap;
  }

  prepareExcelData(): Array<SensitivityExcelData> {
    const excelData: Array<SensitivityExcelData> = [];
    this.micSensitivityMap.forEach((tmic, propagationFactor) => {
      excelData.push(new SensitivityExcelData(propagationFactor, tmic, this.acSensitivityMap.get(propagationFactor)));
    });
    return excelData;
  }
}

export class SensitivityExcelData {
  propagationFactor: number;
  tmic: number;
  tac: number;

  constructor(propagationFactor: number, tmic: number, tac: number) {
    this.propagationFactor = propagationFactor;
    this.tmic = tmic;
    this.tac = tac;
  }
}
