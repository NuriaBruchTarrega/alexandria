import {Component, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {VisualizationComponent} from './visualization/visualization.component';
import {TableVisualizationComponent} from './table-visualization/table-visualization.component';
import {DependencyTree} from '@models/dependencyTree/tree';
import {TreeNode} from '@models/dependencyTree/node';

@Component({
  selector: 'tabs',
  templateUrl: './tabs.component.html',
  styleUrls: ['./tabs.component.css']
})
export class TabsComponent implements OnInit {

  @ViewChild('treeVisualization') treeVisualization: VisualizationComponent;
  @ViewChild('tableVisualization') tableVisualization: TableVisualizationComponent;
  @Output() selectedNodeEvent = new EventEmitter();
  @Output() noNodeSelectedEvent = new EventEmitter();

  activeProgressBar = false;
  hasData = false;

  private toDisplayTree = false;

  constructor() {
  }

  ngOnInit(): void {
  }

  selectNode(libraryName: string) {
    this.treeVisualization.selectNode(libraryName);
    this.tableVisualization.selectNode(libraryName);
  }

  updateVisualization() {
    this.treeVisualization.updateVisualization();
    this.tableVisualization.updateVisualization();
  }

  generateVisualizations(dependencyTree: DependencyTree) {
    this.hasData = true;
    this.treeVisualization.generateVisTree(dependencyTree);
    this.tableVisualization.generateTable(dependencyTree);
  }

  selectedNodeInTree(node: TreeNode) {
    this.tableVisualization.selectNode(node.getLibraryCompleteName());
    this.selectedNodeEvent.emit(node);
  }

  selectedNodeInTable(node: TreeNode) {
    this.treeVisualization.selectNode(node.getLibraryCompleteName());
    this.selectedNodeEvent.emit(node);
  }

  noNodeSelectedInTree() {
    this.tableVisualization.noNodeSelected();
    this.noNodeSelectedEvent.emit();
  }

  noNodeSelectedInTable() {
    this.treeVisualization.noNodeSelected();
    this.noNodeSelectedEvent.emit();
  }

  changedSelectedTab(index: number) {
    this.toDisplayTree = index === 0;
  }

  animationDone() {
    if (this.toDisplayTree) {
      this.treeVisualization.focus();
      this.toDisplayTree = false;
    }
  }
}
