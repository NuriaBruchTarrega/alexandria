import {Component, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {VisualizationComponent} from '@components/analyzer/tabs/visualization/visualization.component';
import {DependencyTree} from '@models/dependencyTree/tree';

@Component({
  selector: 'tabs',
  templateUrl: './tabs.component.html',
  styleUrls: ['./tabs.component.css']
})
export class TabsComponent implements OnInit {
  @ViewChild('treeVisualization') treeVisualization: VisualizationComponent;
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
    // TODO: send select node to visualization to second tab
  }

  updateVisualization() {
    this.hasData = true;
    this.treeVisualization.updateVisualization();
    // TODO: send updateVisualization to second tab
  }

  generateVisualizations(dependencyTree: DependencyTree) {
    this.treeVisualization.generateVisTree(dependencyTree);
    // TODO: send generate tree to second tab
  }
}
