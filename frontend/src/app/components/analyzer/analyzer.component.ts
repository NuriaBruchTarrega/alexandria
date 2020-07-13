import {Component, OnInit, ViewChild} from '@angular/core';
import {AnalyzerService} from '../../services/analyzer.service';
import {Library} from '../../models/library';
import {VisualizationComponent} from './visualization/visualization.component';
import {DependencyTreeFactory} from '../../models/dependencyTree/tree';

@Component({
  selector: 'analyzer',
  templateUrl: './analyzer.component.html',
  styleUrls: ['./analyzer.component.css']
})
export class AnalyzerComponent implements OnInit {
  @ViewChild('treeVisualization') treeVisualization: VisualizationComponent;

  constructor(private analyzerService: AnalyzerService) {
  }

  ngOnInit(): void {
  }

  doAnalyzeRequest(library: Library) {
    this.analyzerService
      .analyzeLibrary(library)
      .subscribe(res => {
        console.log(res);
        this.updateTreeVisualization();
      });
  }

  private updateTreeVisualization() {
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

    this.treeVisualization.generateVisTree(DependencyTreeFactory.create({nodes, edges}));
  }

}
