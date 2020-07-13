import {Component, OnInit, ViewChild} from '@angular/core';
import {AnalyzerService} from '../../services/analyzer.service';
import {VisualizationComponent} from './visualization/visualization.component';
import {FormComponent} from './form/form.component';
import {Library} from '../../models/library';
import {DependencyTree} from '../../models/dependencyTree/tree';

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
      .subscribe(dependencyTree => {
        this.updateTreeVisualization(dependencyTree);
        this.deactivateProgressBar();
      });
  }

  private updateTreeVisualization(dependencyTree: DependencyTree) {
    this.treeVisualization.generateVisTree(dependencyTree);
  }

  private activateProgressBar() {
    // Activate progress bar
    this.treeVisualization.activeProgressBar = true;
    this.libraryForm.isProgressBarActive = true;
  }

  private deactivateProgressBar() {
    // Deactivate progress bar
    this.treeVisualization.activeProgressBar = false;
    this.libraryForm.isProgressBarActive = false;
  }
}
