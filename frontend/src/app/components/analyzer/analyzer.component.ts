import {Component, OnInit, ViewChild} from '@angular/core';
import {AnalyzerService} from '../../services/analyzer.service';
import {VisualizationComponent} from './visualization/visualization.component';
import {FormComponent} from './form/form.component';
import {Library} from '../../models/library';
import {DependencyTreeFactory} from '../../models/dependencyTree/tree';

@Component({
  selector: 'analyzer',
  templateUrl: './analyzer.component.html',
  styleUrls: ['./analyzer.component.css']
})
export class AnalyzerComponent implements OnInit {
  @ViewChild('treeVisualization') treeVisualization: VisualizationComponent;
  @ViewChild('libraryForm') libraryForm: FormComponent;

  constructor(private analyzerService: AnalyzerService) {
  }

  ngOnInit(): void {
  }

  doAnalyzeRequest(library: Library) {
    this.activateProgressBar();
    this.analyzerService
      .analyzeLibrary(library)
      .subscribe(res => {
        console.log(res);
        this.updateTreeVisualization();
        this.deactivateProgressBar();
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

  private activateProgressBar() {
    this.libraryForm.isProgressBarActive = true;
    // Activate progress bar
  }

  private deactivateProgressBar() {
    this.libraryForm.isProgressBarActive = false;
    // Deactivate progress bar
  }
}
