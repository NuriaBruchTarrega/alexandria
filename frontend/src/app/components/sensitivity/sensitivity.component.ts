import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {SensitivityService} from '@services/sensitivity.service';
import {catchError} from 'rxjs/operators';
import {throwError} from 'rxjs';
import {buildError} from '@builders/error.builder';
import {SensitivityResult} from '@models/experiments/sensitivity-result';
import {ExcelService} from '@services/excel.service';

@Component({
  selector: 'sensitivity',
  templateUrl: './sensitivity.component.html',
  styleUrls: ['./sensitivity.component.css']
})
export class SensitivityComponent implements OnInit {
  sensitivityForm: FormGroup;
  private requestOnProcess = false;

  constructor(
    private sensitivityService: SensitivityService,
    private formBuilder: FormBuilder,
    private excelService: ExcelService) {
  }

  ngOnInit(): void {
    this.sensitivityForm = this.formBuilder.group({
      path: ['C:\\Users\\usuario\\Documents\\UvA\\Thesis\\experiments\\sensitivity\\SensitivityData.txt', [Validators.required]]
    });
  }

  onSubmit(formValues) {
    this.requestOnProcess = true;
    this.sensitivityService
      .sensitivityAnalysis(formValues.path)
      .pipe(
        catchError(err => throwError(buildError(err)))
      )
      .subscribe(sensitivityResultSet => {
        this.exportToExcel(sensitivityResultSet);
        this.requestOnProcess = false;
      }, error => {
        this.requestOnProcess = false;
      });
  }

  isButtonDisabled() {
    return this.sensitivityForm.invalid || this.requestOnProcess;
  }

  private exportToExcel(sensitivityResultSet: Set<SensitivityResult>) {
    this.excelService.exportZipFile(sensitivityResultSet);
  }
}
