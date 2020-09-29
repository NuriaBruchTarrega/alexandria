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

  getFileName(): string {
    return this.clientLibrary + '-' + this.serverLibrary;
  }

  getSensitivityTmicExcelData(): SensitivityExcelData[] {
    return this.getSensitivityExcelData(this.micSensitivityMap);
  }

  getSensitivityTacExcelData(): SensitivityExcelData[] {
    return this.getSensitivityExcelData(this.acSensitivityMap);
  }

  getMicInputExcelData(): InputExcelData[] {
    return this.getInputExcelData(this.micValuesMap);
  }

  getAcInputExcelData(): InputExcelData[] {
    return this.getInputExcelData(this.acValuesMap);
  }

  private getSensitivityExcelData(sensitivityMap: Map<number, number>): SensitivityExcelData[] {
    const excelData: SensitivityExcelData[] = [];
    sensitivityMap.forEach((value, propagationFactor) => {
      excelData.push(new SensitivityExcelData(propagationFactor, value));
    });
    return excelData;
  }

  private getInputExcelData(valuesMap: Map<number, number>): InputExcelData[] {
    const excelData: InputExcelData[] = [];
    valuesMap.forEach((value, distance) => {
      excelData.push(new InputExcelData(distance, value));
    });
    return excelData;
  }
}

export class SensitivityExcelData {
  propagationFactor: number;
  value: number;

  constructor(propagationFactor: number, value: number) {
    this.propagationFactor = propagationFactor;
    this.value = value;
  }
}

export class InputExcelData {
  distance: number;
  value: number;

  constructor(distance: number, value: number) {
    this.distance = distance;
    this.value = value;
  }
}
