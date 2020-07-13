import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';
import {Network} from 'vis-network';

@Component({
  selector: 'visualization',
  templateUrl: './visualization.component.html',
  styleUrls: ['./visualization.component.css']
})
export class VisualizationComponent implements AfterViewInit {
  @ViewChild('siteConfigNetwork') networkContainer: ElementRef;
  @ViewChild('svgNetwork') svgNetworkContainer: ElementRef;

  public network: any;

  constructor() {
  }

  ngAfterViewInit(): void {
    const treeData = this.getTreeData();
    this.loadVisTree(treeData);
  }

  loadVisTree(treedata) {
    const options = {
      interaction: {
        hover: true,
      },
      manipulation: {
        enabled: true
      }
    };
    const container = this.networkContainer.nativeElement;
    this.network = new Network(container, treedata, options);

    const that = this;
    this.network.on('hoverNode', params => {
      console.log('hoverNode Event:', params);
    });
    this.network.on('blurNode', params => {
      console.log('blurNode event:', params);
    });
  }

  getTreeData() {
    const nodes = [
      {id: 1, label: 'Node 1', title: 'I am node 1!'},
      {id: 2, label: 'Node 2', title: 'I am node 2!'},
      {id: 3, label: 'Node 3'},
      {id: 4, label: 'Node 4'},
      {id: 5, label: 'Node 5'}
    ];

    // create an array with edges
    const edges = [
      {from: 1, to: 3},
      {from: 1, to: 2},
      {from: 2, to: 4},
      {from: 2, to: 5}
    ];

    const treeData = {
      nodes,
      edges
    };
    return treeData;
  }

}
