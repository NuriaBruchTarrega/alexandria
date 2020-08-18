import {Component, OnInit} from '@angular/core';
import {DependencyTree} from '@models/dependencyTree/tree';
import {TreeNode} from '@models/dependencyTree/node';

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

  selectNodeWithLibraryName(libraryName: string) {
    // TODO: Implementation
  }

  selectNodeWithTreeNode(node: TreeNode) {
    // TODO: Implementation
  }

  updateVisualization() {
    // TODO: Implementation
  }

  generateTable(dependencyTree: DependencyTree) {
    // TODO: Implementation
  }

  noNodeSelected() {
    // TODO: Implementation
  }
}
