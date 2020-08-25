import {isNil} from 'lodash';
import {Component, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {DependencyTree} from '@models/dependencyTree/tree';
import {TreeNode} from '@models/dependencyTree/node';
import {MatTableDataSource} from '@angular/material/table';
import {BloatedDependency, TypeDependency} from '@enumerations/table-filters';
import {MatSort} from '@angular/material/sort';
import {Colors} from '@src/colors';

@Component({
  selector: 'table-visualization',
  templateUrl: './table-visualization.component.html',
  styleUrls: ['./table-visualization.component.css'],
  styles: [`.table-row.selected { background: ${Colors.PINK}; } .table-row:hover { background-color: ${Colors.LIGHTER_BLUE}; }`]
})
export class TableVisualizationComponent implements OnInit {
  @Output() selectedNodeEvent = new EventEmitter();
  @Output() noNodeSelectedEvent = new EventEmitter();

  @ViewChild(MatSort) sort: MatSort;

  displayedColumns: string[] = ['groupId', 'artifactId', 'version', 'level', 'tmic', 'tac', 'classUsage', 'methodUsage'];
  dataSource: MatTableDataSource<TreeNode>;
  clientLibrary: TreeNode;
  selectedNode: TreeNode = null;
  filterByLevel: TypeDependency = TypeDependency.ALL;
  filterByBloated: BloatedDependency = BloatedDependency.ALL;
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
    this.dataSource = new MatTableDataSource<TreeNode>(this.dependencyTree.nodes
      .filter(node => this.checkTypeFilter(node) && this.checkBloatedFilter(node)));

    this.dataSource.sort = this.sort;
  }

  checkNotNaN(num: number) {
    return !isNaN(num);
  }

  private checkTypeFilter(node: TreeNode): boolean {
    if (this.filterByLevel === TypeDependency.ALL) {
      return node.level !== 0;
    }
    if (this.filterByLevel === TypeDependency.DIRECT) {
      return node.level === 1;
    }
    return node.level > 1;
  }

  private checkBloatedFilter(node: TreeNode): boolean {
    if (this.filterByBloated === BloatedDependency.ALL) {
      return true;
    }
    return this.filterByBloated === BloatedDependency.BLOATED ? node.bloated : !node.bloated;
  }
}
