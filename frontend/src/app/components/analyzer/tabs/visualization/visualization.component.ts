import {isNil} from 'lodash';
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

  public network: Network;
  private dependencyTree: DependencyTree;
  selectedNode: IdType = null;

  constructor() {
  }

  ngAfterViewInit(): void {

  }

  generateVisTree(treeData: DependencyTree) {
    this.dependencyTree = treeData;
    this.createTree(treeData);
  }

  selectNode(libraryName: string) {
    this.selectedNode = this.dependencyTree.getNodeIdWithLibraryCompleteName(libraryName);
    this.network.releaseNode();
    this.network.selectNodes([this.selectedNode]);
    this.focusOnSelectedNode();
  }

  updateVisualization() {
    this.network.setData(this.dependencyTree);
    if (!isNil(this.selectedNode)) {
      this.network.selectNodes([this.selectedNode]);
    }
  }

  noNodeSelected() {
    this.selectedNode = null;
    this.focusOnAllGraph();
  }

  focus() {
    isNil(this.selectedNode) ? this.focusOnAllGraph() : this.focusOnSelectedNode();
  }

  private createTree(treeData: DependencyTree) {
    const container = this.networkContainer.nativeElement;
    this.network = new Network(container, treeData, options);
    this.network.on('click', _ => this.clickEvent());
    this.network.on('doubleClick', _ => this.doubleClickEvent());
    this.network.once('beforeDrawing', _ => this.focusOnAllGraph());
  }

  private clickEvent() {
    const selectedNodes: IdType[] = this.network.getSelectedNodes();
    if (selectedNodes.length === 1) {
      this.selectedNode = selectedNodes[0];
      this.focusOnSelectedNode();
    } else if (selectedNodes.length === 0 && !isNil(this.selectedNode)) {
      this.noNodeSelected();
    }
  }

  private doubleClickEvent() {
    if (!isNil(this.selectedNode)) {
      this.dependencyTree.getNodeBranch(this.selectedNode);
    } else {
      this.dependencyTree.displayAllTree();
    }
    this.updateVisualization();
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
      nodes: this.dependencyTree.getNotHiddenNodeIds(),
      animation: {
        duration: 1000,
        easingFunction: 'easeInOutQuad',
      }
    };
    this.network.fit(fitOptions);
    this.noNodeSelectedEvent.emit();
  }
}
