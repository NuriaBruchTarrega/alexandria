import {Injectable} from '@angular/core';
import * as FileSaver from 'file-saver';
import * as XLSX from 'xlsx';
import JSZip from 'jszip';
import {SensitivityResult} from '@models/experiments/sensitivity-result';
import {BenchmarkResult} from '@models/experiments/benchmark-result';

const EXCEL_TYPE = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
const EXCEL_EXTENSION = '.xlsx';

@Injectable({
  providedIn: 'root'
})
export class ExcelService {

  constructor() {
  }

  public exportBenchmarkFile(benchmarkResult: BenchmarkResult): void {
    const directDependenciesWorkSheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(benchmarkResult.directDependencies);
    const transitiveDependenciesMicWorkSheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(benchmarkResult.transitiveDependenciesMic);
    const transitiveDependenciesAcWorkSheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(benchmarkResult.transitiveDependenciesAc);

    const workbook: XLSX.WorkBook = {
      Sheets: {
        MicAc: directDependenciesWorkSheet,
        Tmic: transitiveDependenciesMicWorkSheet,
        Tac: transitiveDependenciesAcWorkSheet,
      }, SheetNames: ['MicAc', 'Tmic', 'Tac']
    };
    const content = XLSX.write(workbook, {bookType: 'xlsx', type: 'array'});

    this.saveExcelFile(content, 'benchmark');
  }

  public exportSensitivityZipFile(sensitivityResultSet: Set<SensitivityResult>): void {
    const zip = new JSZip();

    sensitivityResultSet.forEach(sensitivityResult => {
      const tmicSensitivity = sensitivityResult.getSensitivityTmicExcelData();
      const tacSensitivity = sensitivityResult.getSensitivityTacExcelData();
      const tmicInput = sensitivityResult.getMicInputExcelData();
      const tacInput = sensitivityResult.getAcInputExcelData();
      zip.file(
        sensitivityResult.getFileName() + EXCEL_EXTENSION,
        this.generateSensitivityExcelFiles(tmicSensitivity, tacSensitivity, tmicInput, tacInput));
    });
    zip.generateAsync({type: 'blob'}).then((blob) => {
      FileSaver.saveAs(blob, 'sensitivity.zip');
    });
  }

  generateSensitivityExcelFiles(sensitivityTmic: any[], sensitivityTac: any[], inputTmic: any[], inputTac: any[]): any {
    const sheetNames = this.createSensitivitySheetNames(inputTmic, inputTac);

    const sensitivityTmicWorkSheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(sensitivityTmic);
    const inputTmicWorkSheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(inputTmic);
    const sensitivityTacWorkSheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(sensitivityTac);
    const inputTacWorkSheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(inputTac);

    const workbook: XLSX.WorkBook = {
      Sheets: {
        sensitivityTmic: sensitivityTmicWorkSheet,
        inputTmic: inputTmicWorkSheet,
        sensitivityTac: sensitivityTacWorkSheet,
        inputTac: inputTacWorkSheet
      }, SheetNames: sheetNames
    };

    return XLSX.write(workbook, {bookType: 'xlsx', type: 'array'});
  }

  public exportExcelFile(json: any[], fileName: string): void {
    const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(json);
    const workbook: XLSX.WorkBook = {Sheets: {data: worksheet}, SheetNames: ['data']};
    const excelBuffer: any = XLSX.write(workbook, {bookType: 'xlsx', type: 'array'});
    this.saveExcelFile(excelBuffer, fileName);
  }

  private createSensitivitySheetNames(inputTmic: any[], inputTac: any[]): any[] {
    const sheetNames = [];

    if (inputTmic.length !== 0) {
      sheetNames.push('sensitivityTmic');
      sheetNames.push('inputTmic');
    }
    if (inputTac.length !== 0) {
      sheetNames.push('sensitivityTac');
      sheetNames.push('inputTac');
    }

    return sheetNames;
  }

  private saveExcelFile(buffer: any, fileName: string) {
    const data: Blob = new Blob([buffer], {type: EXCEL_TYPE});
    FileSaver.saveAs(data, fileName + EXCEL_EXTENSION);
  }
}
