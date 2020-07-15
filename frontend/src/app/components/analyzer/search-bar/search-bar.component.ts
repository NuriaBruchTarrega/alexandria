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
    console.log('HELO');
    this.currentLibraries = libraries;
  }

  selectedLibrary() {
    const library = this.value.trim();
    if (this.currentLibraries.find(currentLibrary => currentLibrary === library)) {
      // Emit selected library
      this.value = '';
    }
  }

  onType($event) {
    if ($event.key !== 'Enter') {
      const search = this.value.trim();
      if (search.length === 0) {
        this.filteredLibraries = [];
      } else {
        this.filterLibraries(search);
      }
    }
  }

  private filterLibraries(search: string) {
    this.filteredLibraries = this.currentLibraries.filter(library => library.includes(search));
  }
}
