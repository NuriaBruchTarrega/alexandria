import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.css']
})
export class SearchBarComponent implements OnInit {

  value: string;
  filteredLibraries = [];
  private currentLibraries: string[];

  constructor() {
  }

  ngOnInit(): void {
  }

  setCurrentLibraries(libraries: string[]) {
    this.currentLibraries = libraries;
  }


}
