import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.css']
})
export class SearchBarComponent implements OnInit {

  value: string;
  filteredOptions = ['One', 'Two', 'Three'];

  constructor() {
  }

  ngOnInit(): void {
  }

}
