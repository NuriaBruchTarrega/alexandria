import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css']
})
export class FormComponent implements OnInit {
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
  }
}
