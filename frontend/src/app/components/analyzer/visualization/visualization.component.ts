import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';
import {Network} from 'vis-network';
import {options} from './options';
import {DependencyTree, DependencyTreeFactory} from '../../../models/dependencyTree/tree';

@Component({
  selector: 'visualization',
  templateUrl: './visualization.component.html',
  styleUrls: ['./visualization.component.css']
})
export class VisualizationComponent implements AfterViewInit {
  @ViewChild('dependencyTreeNetwork') networkContainer: ElementRef;

  public network: any;

  constructor() {
  }

  ngAfterViewInit(): void {

  }

  generateVisTree(treeData: DependencyTree) {
    const container = this.networkContainer.nativeElement;
    this.network = new Network(container, treeData, options);

    this.network.on('hoverNode', params => {
      console.log('hoverNode Event:', params);
    });
    this.network.on('blurNode', params => {
      console.log('blurNode event:', params);
    });
  }

  getTreeData(): DependencyTree {
    const nodes = [
      {id: 1, label: 'Node 1', title: 'I am node 1!'},
      {id: 2, label: 'Node 2', title: 'I am node 2!'},
      {id: 3, label: 'Node 3'},
      {id: 4, label: 'Node 4'},
      {id: 5, label: 'Node 5'}
    ];

    const edges = [
      {from: 1, to: 3},
      {from: 1, to: 2},
      {from: 2, to: 4},
      {from: 2, to: 5}
    ];

    return DependencyTreeFactory.create({
      nodes,
      edges
    });
  }
}
