import {Injectable} from '@angular/core';
import * as FileSaver from 'file-saver';
import * as XLSX from 'xlsx';
import JSZip from 'jszip';
import {SensitivityResult} from '@models/experiments/sensitivity-result';

const EXCEL_TYPE = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
const EXCEL_EXTENSION = '.xlsx';

@Injectable({
  providedIn: 'root'
})
export class ExcelService {

  constructor() {
  }

  private static createSheetNames(inputTmic: any[], inputTac: any[]): any[] {
    const sheetNames = [];

    if (inputTmic.length !== 0) {
      sheetNames.push(['sensitivityTmic', 'inputTmic']);
    }
    if (inputTac.length !== 0) {
      sheetNames.push(['sensitivityTac', 'inputTac']);
    }

    return sheetNames;
  }

  public exportZipFile(sensitivityResultSet: Set<SensitivityResult>): void {
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
    zip.generateAsync({type: 'blob'}).then((blob) => { // 1) generate the zip file
      FileSaver.saveAs(blob, 'sensitivity.zip');                          // 2) trigger the download
    });
  }

  generateSensitivityExcelFiles(sensitivityTmic: any[], sensitivityTac: any[], inputTmic: any[], inputTac: any[]): any {
    const sheetNames = ExcelService.createSheetNames(inputTmic, inputTac);

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

  saveExcelFile(buffer: any, fileName: string) {
    const data: Blob = new Blob([buffer], {type: EXCEL_TYPE});
    FileSaver.saveAs(data, fileName + EXCEL_EXTENSION);
  }
}
