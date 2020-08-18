import {AfterViewInit, Component, ElementRef, EventEmitter, Output, ViewChild} from '@angular/core';
import {FitOptions, FocusOptions, IdType, Network} from 'vis-network';
import {options} from './options';
import {DependencyTree} from '@models/dependencyTree/tree';

@Component({
  selector: 'visualization',
  templateUrl: './visualization.component.html',
  styleUrls: ['./visualization.component.css']
})
export class VisualizationComponent implements AfterViewInit {
  @ViewChild('dependencyTreeNetwork') networkContainer: ElementRef;
  @Output() selectedNodeEvent = new EventEmitter();
  @Output() noNodeSelectedEvent = new EventEmitter();

  activeProgressBar = false;
  public network: Network;
  private dependencyTree: DependencyTree;
  selectedNode: IdType = null;

  constructor() {
  }

  ngAfterViewInit(): void {

  }

  generateVisTree(treeData: DependencyTree) {
    this.dependencyTree = treeData;
    const container = this.networkContainer.nativeElement;
    this.network = new Network(container, treeData, options);
    this.network.on('click', _ => this.clickEvent());
    this.network.once('beforeDrawing', _ => this.focusOnAllGraph());
  }

  selectNode(libraryName: string) {
    this.selectedNode = this.dependencyTree.getNodeIdWithLibraryCompleteName(libraryName);
    this.network.releaseNode();
    this.network.selectNodes([this.selectedNode]);
    this.focusOnSelectedNode();
  }

  private clickEvent() {
    const selectedNodes: IdType[] = this.network.getSelectedNodes();
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
      scale: 1,
      animation: {
        duration: 1000,
        easingFunction: 'easeInOutQuad',
      }
    };
    this.network.focus(this.selectedNode, focusOptions);
    this.selectedNodeEvent.emit(this.dependencyTree.getNodeById(this.selectedNode));
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
    this.noNodeSelectedEvent.emit();
  }

  updateVisualization() {
    this.network.setData(this.dependencyTree);
    this.selectedNode = null;
  }
}
