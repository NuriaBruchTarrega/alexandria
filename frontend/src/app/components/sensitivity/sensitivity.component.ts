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

  constructor(private sensitivityService: SensitivityService, private formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    this.sensitivityForm = this.formBuilder.group({
      path: ['', [Validators.required]]
    });
  }

  onSubmit(formValues) {
    this.sensitivityService
      .sensitivityAnalysis(formValues.path)
      .pipe(
        catchError(err => throwError(buildError(err)))
      )
      .subscribe(sensitivityAnalysisResult => {
        // TODO: implement
      });
  }
}
