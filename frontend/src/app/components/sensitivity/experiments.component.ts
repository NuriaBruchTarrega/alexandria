import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ExperimentsService} from '@services/experiments.service';
import {catchError} from 'rxjs/operators';
import {throwError} from 'rxjs';
import {buildError} from '@builders/error.builder';
import {SensitivityResult} from '@models/experiments/sensitivity-result';
import {ExcelService} from '@services/excel.service';

@Component({
  selector: 'sensitivity',
  templateUrl: './experiments.component.html',
  styleUrls: ['./experiments.component.css']
})
export class ExperimentsComponent implements OnInit {
  sensitivityForm: FormGroup;
  private requestOnProcess = false;

  constructor(
    private experimentsService: ExperimentsService,
    private formBuilder: FormBuilder,
    private excelService: ExcelService) {
  }

  ngOnInit(): void {
    this.sensitivityForm = this.formBuilder.group({
      path: ['C:\\Users\\usuario\\Documents\\UvA\\Thesis\\experiments\\sensitivity\\SensitivityData.txt', [Validators.required]]
    });
  }

  onSubmitSensitivity(formValues) {
    this.requestOnProcess = true;
    this.experimentsService
      .sensitivityAnalysis(formValues.path)
      .pipe(
        catchError(err => throwError(buildError(err)))
      )
      .subscribe(sensitivityResultSet => {
        this.exportSensitivityAnalysisToExcel(sensitivityResultSet);
        this.requestOnProcess = false;
      }, error => {
        this.requestOnProcess = false;
      });
  }

  isSensitivityDisabled() {
    return this.sensitivityForm.invalid || this.requestOnProcess;
  }

  private exportSensitivityAnalysisToExcel(sensitivityResultSet: Set<SensitivityResult>) {
    this.excelService.exportZipFile(sensitivityResultSet);
  }
}
