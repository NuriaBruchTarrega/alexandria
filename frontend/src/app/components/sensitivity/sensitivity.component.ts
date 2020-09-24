import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {SensitivityService} from '@services/sensitivity.service';
import {catchError} from 'rxjs/operators';
import {throwError} from 'rxjs';
import {buildError} from '@builders/error.builder';

@Component({
  selector: 'sensitivity',
  templateUrl: './sensitivity.component.html',
  styleUrls: ['./sensitivity.component.css']
})
export class SensitivityComponent implements OnInit {
  sensitivityForm: FormGroup;
  private requestOnProcess = false;

  constructor(private sensitivityService: SensitivityService, private formBuilder: FormBuilder) {
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
      .subscribe(sensitivityAnalysisResult => {
        this.requestOnProcess = false;
        // TODO: implement
      }, error => {
        this.requestOnProcess = false;
      });
  }

  isButtonDisabled() {
    return this.sensitivityForm.invalid || this.requestOnProcess;
  }
}
