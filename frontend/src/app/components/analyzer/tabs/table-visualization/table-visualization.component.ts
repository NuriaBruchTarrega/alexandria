import {Component, OnInit} from '@angular/core';
import {DependencyTree} from '@models/dependencyTree/tree';

@Component({
  selector: 'table-visualization',
  templateUrl: './table-visualization.component.html',
  styleUrls: ['./table-visualization.component.css']
})
export class TableVisualizationComponent implements OnInit {

  constructor() {
  }

  ngOnInit(): void {
  }

  selectNode(libraryName: string) {
    // TODO: Implementation
  }

  updateVisualization() {
    // TODO: Implementation
  }

  generateTable(dependencyTree: DependencyTree) {
    // TODO: Implementation
  }
}
