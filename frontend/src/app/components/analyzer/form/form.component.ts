import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Library, LibraryFactory} from '../../../models/library';

@Component({
  selector: 'form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css']
})
export class FormComponent implements OnInit {
  @Output() analyzeLibraryEvent = new EventEmitter();

  title = 'Client library';
  groupID: string;
  artifactID: string;
  version: string;

  constructor() {
  }

  ngOnInit(): void {
  }

  onClick() {
    // Here is where the form should be sent
    const library: Library = LibraryFactory.create({groupID: this.groupID, artifactID: this.artifactID, version: this.version});
    this.analyzeLibraryEvent.emit(library);
  }
}
