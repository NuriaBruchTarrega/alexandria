import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ExperimentsService} from '@services/experiments.service';
import {catchError} from 'rxjs/operators';
import {throwError} from 'rxjs';
import {buildError} from '@builders/error.builder';
import {SensitivityResult} from '@models/experiments/sensitivity-result';
import {ExcelService} from '@services/excel.service';
import {BenchmarkResult} from '@models/experiments/benchmark-result';

@Component({
  selector: 'sensitivity',
  templateUrl: './experiments.component.html',
  styleUrls: ['./experiments.component.css']
})
export class ExperimentsComponent implements OnInit {
  sensitivityForm: FormGroup;
  benchMarkForm: FormGroup;
  private sensitivityRequestOnProcess = false;
  private benchmarkRequestOnProcess = false;

  constructor(
    private experimentsService: ExperimentsService,
    private formBuilder: FormBuilder,
    private excelService: ExcelService) {
  }

  ngOnInit(): void {
    this.sensitivityForm = this.formBuilder.group({
      path: ['C:\\Users\\usuario\\Documents\\UvA\\Thesis\\experiments\\sensitivity\\SensitivityData.txt', [Validators.required]]
    });
    this.benchMarkForm = this.formBuilder.group({
      path: ['C:\\Users\\usuario\\Documents\\UvA\\Thesis\\experiments\\benchmark\\BenchmarkData.txt', [Validators.required]]
    });
  }

  onSubmitSensitivity(formValues) {
    this.sensitivityRequestOnProcess = true;
    this.experimentsService
      .sensitivityAnalysis(formValues.path)
      .pipe(
        catchError(err => throwError(buildError(err)))
      )
      .subscribe(sensitivityResultSet => {
        this.exportSensitivityAnalysisToExcel(sensitivityResultSet);
        this.sensitivityRequestOnProcess = false;
      }, error => {
        this.sensitivityRequestOnProcess = false;
      });
  }

  onSubmitBenchMark(formValues) {
    this.benchmarkRequestOnProcess = true;
    this.experimentsService
      .benchmarkRequest(formValues.path)
      .pipe(
        catchError(err => throwError(buildError(err)))
      )
      .subscribe(result => {
        this.exportBenchmarkResultsToExcel(result);
        this.benchmarkRequestOnProcess = false;
      }, error => {
        this.benchmarkRequestOnProcess = false;
      });
  }

  isSensitivityDisabled() {
    return this.sensitivityForm.invalid || this.sensitivityRequestOnProcess;
  }

  isBenchMarkDisabled() {
    return this.benchMarkForm.invalid || this.benchmarkRequestOnProcess;
  }

  private exportSensitivityAnalysisToExcel(sensitivityResultSet: Set<SensitivityResult>) {
    this.excelService.exportSensitivityZipFile(sensitivityResultSet);
  }

  private exportBenchmarkResultsToExcel(result: BenchmarkResult) {
    this.excelService.exportBenchmarkFile(result);
  }
}
