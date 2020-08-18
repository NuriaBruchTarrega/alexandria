import {Component, OnInit} from '@angular/core';
import {DependencyTree} from '@models/dependencyTree/tree';
import {TreeNode} from '@models/dependencyTree/node';
import {MatTableDataSource} from '@angular/material/table';

@Component({
  selector: 'table-visualization',
  templateUrl: './table-visualization.component.html',
  styleUrls: ['./table-visualization.component.css']
})
export class TableVisualizationComponent implements OnInit {

  displayedColumns: string[] = ['groupId', 'artifactId', 'version', 'type', 'mic', 'ac', 'annotations', '%Classes', '%Methods'];
  dataSource: MatTableDataSource<TreeNode>;
  private dependencyTree: DependencyTree;
  clientLibrary: TreeNode;

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
    this.dependencyTree = dependencyTree;
    this.clientLibrary = dependencyTree.nodes.find(node => node.level === 0);
    this.dataSource = new MatTableDataSource(dependencyTree.nodes.filter(node => node.level !== 0));
  }

  noNodeSelected() {
    // TODO: Implementation
  }
}
