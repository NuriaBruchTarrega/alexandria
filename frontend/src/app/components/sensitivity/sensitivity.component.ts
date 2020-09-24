import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'sensitivity',
  templateUrl: './sensitivity.component.html',
  styleUrls: ['./sensitivity.component.css']
})
export class SensitivityComponent implements OnInit {
  sensitivityForm: FormGroup;

  constructor(private formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    this.sensitivityForm = this.formBuilder.group({
      path: ['', [Validators.required]]
    });
  }

  onSubmit(formValues) {
    // Do request
  }
}
