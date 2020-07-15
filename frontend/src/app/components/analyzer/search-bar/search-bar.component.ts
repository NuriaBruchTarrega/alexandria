import {Component, EventEmitter, OnInit, Output} from '@angular/core';

@Component({
  selector: 'search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.css']
})
export class SearchBarComponent implements OnInit {
  @Output() selectedLibrary = new EventEmitter();

  value: string;
  filteredLibraries = [];
  private currentLibraries: string[] = [];

  constructor() {
  }

  ngOnInit(): void {
  }

  setCurrentLibraries(libraries: string[]) {
    this.currentLibraries = libraries;
  }

  onSelectLibrary() {
    if (this.currentLibraries.length === 0) {
      return;
    }
    const library = this.value.trim();
    if (this.currentLibraries.find(currentLibrary => currentLibrary === library)) {
      this.selectedLibrary.emit(library);
      this.filteredLibraries = [];
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
    if (this.currentLibraries.length === 0) {
      return;
    }
    this.filteredLibraries = this.currentLibraries.filter(library => library.includes(search));
  }
}
