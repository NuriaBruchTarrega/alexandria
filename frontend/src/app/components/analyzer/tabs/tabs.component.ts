import {Component, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {VisualizationComponent} from './visualization/visualization.component';
import {TableVisualizationComponent} from './table-visualization/table-visualization.component';
import {DependencyTree} from '@models/dependencyTree/tree';

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
}
