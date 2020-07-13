import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';
import {FitOptions, FocusOptions, Network} from 'vis-network';
import {options} from './options';
import {DependencyTree} from '../../../models/dependencyTree/tree';

@Component({
  selector: 'visualization',
  templateUrl: './visualization.component.html',
  styleUrls: ['./visualization.component.css']
})
export class VisualizationComponent implements AfterViewInit {
  @ViewChild('dependencyTreeNetwork') networkContainer: ElementRef;

  activeProgressBar = false;
  public network: any;
  private dependencyTree: DependencyTree;
  selectedNode: number = null;

  constructor() {
  }

  ngAfterViewInit(): void {

  }

  generateVisTree(treeData: DependencyTree) {
    this.dependencyTree = treeData;
    const container = this.networkContainer.nativeElement;
    this.network = new Network(container, treeData, options);
    this.network.on('click', _ => this.clickEvent());
  }

  private clickEvent() {
    const selectedNodes: number[] = this.network.getSelectedNodes();
    if (selectedNodes.length === 1) {
      this.selectedNode = selectedNodes[0];
      this.focusOnSelectedNode();
    } else if (selectedNodes.length === 0 && this.selectedNode !== null) {
      this.selectedNode = null;
      this.focusOnAllGraph();
    }
  }

  private focusOnSelectedNode() {
    const focusOptions: FocusOptions = {
      animation: {
        duration: 1000,
        easingFunction: 'easeInOutQuad',
      }
    };
    this.network.focus(this.selectedNode, focusOptions);
  }

  private focusOnAllGraph() {
    const fitOptions: FitOptions = {
      nodes: this.dependencyTree.getAllNodeIds(),
      animation: {
        duration: 1000,
        easingFunction: 'easeInOutQuad',
      }
    };
    this.network.fit(fitOptions);
  }
}
