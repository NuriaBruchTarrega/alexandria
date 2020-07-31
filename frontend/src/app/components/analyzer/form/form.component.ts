import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Library, LibraryFactory} from '../../../models/library';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css']
})
export class FormComponent implements OnInit {
  @Output() analyzeLibraryEvent = new EventEmitter();

  title = 'Client library';
  isProgressBarActive = false;
  libraryForm: FormGroup;

  constructor(private formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    this.libraryForm = this.formBuilder.group({
      groupID: ['org.neo4j', [Validators.required]],
      artifactID: ['neo4j-collections', Validators.required],
      version: ['3.5.12', [Validators.required]]
    });
  }

  onSubmit(formValues) {
    const {groupID, artifactID, version} = formValues;
    const library: Library = LibraryFactory.create({groupID, artifactID, version});
    this.analyzeLibraryEvent.emit(library);
  }

  isButtonDisabled() {
    if (this.libraryForm.invalid) {
      return true;
    }

    return this.isProgressBarActive;
  }
}
