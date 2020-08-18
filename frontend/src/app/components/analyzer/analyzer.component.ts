import {Component, OnInit, ViewChild} from '@angular/core';
import {isNil} from 'lodash';
import {AnalyzerService} from '@services/analyzer.service';
import {FormComponent} from './form/form.component';
import {Library} from '@models/library';
import {DependencyTree} from '@models/dependencyTree/tree';
import {SearchBarComponent} from './search-bar/search-bar.component';
import {MatSnackBar} from '@angular/material/snack-bar';
import {catchError} from 'rxjs/operators';
import {throwError} from 'rxjs';
import {buildError} from '@builders/error.builder';
import {CalculatorComponent} from './calculator/calculator.component';
import {Metrics} from '@enumerations/metrics';
import {TreeNodeService} from '@services/tree.node.service';
import {ClassDistributionComponent} from './class-distribution/class-distribution.component';
import {TreeNode} from '@models/dependencyTree/node';
import {TabsComponent} from '@components/analyzer/tabs/tabs.component';
import {ChartData} from '@models/chart.data';

@Component({
  selector: 'analyzer',
  templateUrl: './analyzer.component.html',
  styleUrls: ['./analyzer.component.css']
})
export class AnalyzerComponent implements OnInit {
  @ViewChild('tabsComponent') tabsComponent: TabsComponent;
  @ViewChild('libraryForm') libraryForm: FormComponent;
  @ViewChild('searchBar') searchBar: SearchBarComponent;
  @ViewChild('calculator') calculator: CalculatorComponent;
  @ViewChild('classDistribution') classDistribution: ClassDistributionComponent;

  Metrics = Metrics;
  private dependencyTree: DependencyTree;

  constructor(
    private analyzerService: AnalyzerService,
    private treeNodeService: TreeNodeService,
    protected snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
  }

  selectedLibrary(libraryName: string) {
    this.tabsComponent.selectNode(libraryName);
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
        this.tabsComponent.hasData = false;
      });
  }

  formulaFactorChanged(metric: Metrics, factor: number) {
    if (!isNil(this.dependencyTree)) {
      this.treeNodeService.calculateMetric(this.dependencyTree, metric, factor);
      this.tabsComponent.updateVisualization();
      this.deleteClassDistribution();
    }
  }

  createClassDistribution(node: TreeNode) {
    const chartData: ChartData[] = this.treeNodeService.generateChartData(node);
    this.classDistribution.updateClassDistributionData(chartData);
  }

  deleteClassDistribution() {
    this.classDistribution.updateClassDistributionData([]);
  }

  private calculateInitialMetrics() {
    this.treeNodeService.calculateMetric(this.dependencyTree, Metrics.Tmic, this.calculator.tmicX);
    this.treeNodeService.calculateMetric(this.dependencyTree, Metrics.Tac, this.calculator.tacX);
    // TODO: new factor for annotations
    this.treeNodeService.calculateMetric(this.dependencyTree, Metrics.Tannotations, this.calculator.tacX);
  }

  private updateTreeVisualization(dependencyTree: DependencyTree) {
    this.tabsComponent.generateVisualizations(dependencyTree);
  }

  private activateProgressBar() {
    this.tabsComponent.activeProgressBar = true;
    this.libraryForm.isProgressBarActive = true;
  }

  private deactivateProgressBar() {
    this.tabsComponent.activeProgressBar = false;
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
