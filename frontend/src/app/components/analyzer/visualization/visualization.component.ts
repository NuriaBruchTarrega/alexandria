import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';
import {Network} from 'vis-network';
import {options} from './options';
import {DependencyTree} from '../../../models/dependencyTree/tree';

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
}
