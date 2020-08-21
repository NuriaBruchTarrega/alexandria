import {isNil} from 'lodash';
import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {DependencyTree} from '@models/dependencyTree/tree';
import {TreeNode} from '@models/dependencyTree/node';
import {MatTableDataSource} from '@angular/material/table';
import {TypeDependency} from '@enumerations/table-filters';

@Component({
  selector: 'table-visualization',
  templateUrl: './table-visualization.component.html',
  styleUrls: ['./table-visualization.component.css']
})
export class TableVisualizationComponent implements OnInit {
  @Output() selectedNodeEvent = new EventEmitter();
  @Output() noNodeSelectedEvent = new EventEmitter();

  displayedColumns: string[] = ['groupId', 'artifactId', 'version', 'type', 'mic', 'ac', '%Classes', '%Methods'];
  dataSource: MatTableDataSource<TreeNode>;
  clientLibrary: TreeNode;
  selectedNode: TreeNode = null;
  filter: TypeDependency = TypeDependency.ALL;
  private dependencyTree: DependencyTree;

  constructor() {
  }

  ngOnInit(): void {
  }

  selectNode(libraryName: string) {
    const selectedNodeId: number = this.dependencyTree.getNodeIdWithLibraryCompleteName(libraryName);
    this.selectedNode = this.dependencyTree.getNodeById(selectedNodeId);
  }

  updateVisualization() {
    this.generateTable(this.dependencyTree);
  }

  generateTable(dependencyTree: DependencyTree) {
    this.dependencyTree = dependencyTree;
    this.clientLibrary = dependencyTree.nodes.find(node => node.level === 0);
    this.changedFilter();
  }

  noNodeSelected() {
    this.selectedNode = null;
  }

  clickedRow(clicked: TreeNode) {
    if (!isNil(this.selectedNode) && this.selectedNode.id === clicked.id) {
      this.unselectNode();
    } else {
      this.selectedNode = clicked;
      this.selectedNodeEvent.emit(clicked);
    }
  }

  private unselectNode() {
    this.noNodeSelectedEvent.emit();
  }

  isSelected(row: TreeNode) {
    return !isNil(this.selectedNode) && row.id === this.selectedNode.id ? 'selected' : '';
  }

  changedFilter() {
    if (this.filter === TypeDependency.ALL) {
      this.dataSource = new MatTableDataSource(this.dependencyTree.nodes.filter(node => node.level !== 0));
    } else if (this.filter === TypeDependency.DIRECT) {
      this.dataSource = new MatTableDataSource(this.dependencyTree.nodes.filter(node => node.level === 1));
    } else {
      this.dataSource = new MatTableDataSource(this.dependencyTree.nodes.filter(node => node.level > 1));
    }
  }
}
