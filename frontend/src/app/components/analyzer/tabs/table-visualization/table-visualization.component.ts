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
  clientLibrary: TreeNode;
  selectedNode: TreeNode = null;
  private dependencyTree: DependencyTree;

  constructor() {
  }

  ngOnInit(): void {
  }

  selectNodeWithLibraryName(libraryName: string) {
    const selectedNodeId: number = this.dependencyTree.getNodeIdWithLibraryCompleteName(libraryName);
    this.selectedNode = this.dependencyTree.getNodeById(selectedNodeId);
  }

  selectNodeWithTreeNode(node: TreeNode) {
    this.selectedNode = node;
  }

  updateVisualization() {
    this.generateTable(this.dependencyTree);
  }

  generateTable(dependencyTree: DependencyTree) {
    this.dependencyTree = dependencyTree;
    this.clientLibrary = dependencyTree.nodes.find(node => node.level === 0);
    this.dataSource = new MatTableDataSource(dependencyTree.nodes.filter(node => node.level !== 0));
  }

  noNodeSelected() {
    this.selectedNode = null;
  }
}
