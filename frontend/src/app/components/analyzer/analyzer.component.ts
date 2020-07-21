import {Component, OnInit, ViewChild} from '@angular/core';
import {isNil} from 'lodash';
import {AnalyzerService} from '../../services/analyzer.service';
import {VisualizationComponent} from './visualization/visualization.component';
import {FormComponent} from './form/form.component';
import {Library} from '../../models/library';
import {DependencyTree} from '../../models/dependencyTree/tree';
import {SearchBarComponent} from './search-bar/search-bar.component';
import {MatSnackBar} from '@angular/material/snack-bar';
import {catchError} from 'rxjs/operators';
import {throwError} from 'rxjs';
import {buildError} from '../../builders/error.builder';
import {CalculatorComponent} from './calculator/calculator.component';
import {Metrics} from '../../enumerations/metrics';
import {CalculatorService} from '../../services/calculator.service';

@Component({
  selector: 'analyzer',
  templateUrl: './analyzer.component.html',
  styleUrls: ['./analyzer.component.css']
})
export class AnalyzerComponent implements OnInit {
  @ViewChild('treeVisualization') treeVisualization: VisualizationComponent;
  @ViewChild('libraryForm') libraryForm: FormComponent;
  @ViewChild('searchBar') searchBar: SearchBarComponent;
  @ViewChild('calculator') calculator: CalculatorComponent;

  Metrics = Metrics;
  private dependencyTree: DependencyTree;

  constructor(
    private analyzerService: AnalyzerService,
    private calculatorService: CalculatorService,
    protected snackBar: MatSnackBar) {
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
      .pipe(
        catchError(err => throwError(buildError(err)))
      )
      .subscribe(dependencyTree => {
        if (dependencyTree instanceof DependencyTree) {
          this.dependencyTree = dependencyTree;
          this.calculateInitialMetrics();
          this.updateTreeVisualization(dependencyTree);
          this.searchBar.setCurrentLibraries(dependencyTree.getLibrariesCompleteNames());
          this.deactivateProgressBar();
        }
      }, error => {
        this.handleRequestErrors(error);
      });
  }

  formulaFactorChanged(metric: Metrics, factor: number) {
    if (!isNil(this.dependencyTree)) {
      this.calculatorService.calculateMetric(this.dependencyTree, metric, factor);
      this.treeVisualization.updateVisualization();
    }
  }

  private calculateInitialMetrics() {
    this.calculatorService.calculateMetric(this.dependencyTree, Metrics.Tmic, 1);
    this.calculatorService.calculateMetric(this.dependencyTree, Metrics.Tac, 1);
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
    this.deactivateProgressBar();
    const message = error.status ? `${error.status}: ${error.message}` : error.message;
    this.snackBar.open(message, 'Dismiss', {
      duration: 4000,
      panelClass: ['my-snack-bar']
    });
  }
}
