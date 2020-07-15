import {Component, OnInit, ViewChild} from '@angular/core';
import {AnalyzerService} from '../../services/analyzer.service';
import {VisualizationComponent} from './visualization/visualization.component';
import {FormComponent} from './form/form.component';
import {Library} from '../../models/library';
import {DependencyTree} from '../../models/dependencyTree/tree';
import {SearchBarComponent} from './search-bar/search-bar.component';

@Component({
  selector: 'analyzer',
  templateUrl: './analyzer.component.html',
  styleUrls: ['./analyzer.component.css']
})
export class AnalyzerComponent implements OnInit {
  @ViewChild('treeVisualization') treeVisualization: VisualizationComponent;
  @ViewChild('libraryForm') libraryForm: FormComponent;
  @ViewChild('searchBar') searchBar: SearchBarComponent;

  constructor(private analyzerService: AnalyzerService) {
  }

  ngOnInit(): void {
  }

  selectedLibrary(libraryName: string) {
    this.treeVisualization.selectNode(libraryName);
  }

  doAnalyzeRequest(library: Library) {
    this.activateProgressBar();
    this.analyzerService
      .analyzeLibrary(library)
      .subscribe(dependencyTree => {
        if (dependencyTree instanceof DependencyTree) {
          this.updateTreeVisualization(dependencyTree);
          this.searchBar.setCurrentLibraries(dependencyTree.getLibrariesCompleteNames());
          this.deactivateProgressBar();
        }
      }, error => {
        this.handleRequestErrors(error);
      });
  }

  private updateTreeVisualization(dependencyTree: DependencyTree) {
    this.treeVisualization.generateVisTree(dependencyTree);
  }

  private activateProgressBar() {
    this.treeVisualization.activeProgressBar = true;
    this.libraryForm.isProgressBarActive = true;
  }

  private deactivateProgressBar() {
    this.treeVisualization.activeProgressBar = false;
    this.libraryForm.isProgressBarActive = false;
  }

  private handleRequestErrors(error) {
    // Manage errors in the request
  }
}
