import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppRoutingModule} from './app-routing.module';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgxChartsModule} from '@swimlane/ngx-charts';

import {MAT_MODULES} from './angular.material';

import {BaseComponent} from '@components/base/base.component';
import {AnalyzerComponent} from '@components/analyzer/analyzer.component';
import {FormComponent} from '@components/analyzer/form/form.component';
import {VisualizationComponent} from '@components/analyzer/tabs/visualization/visualization.component';
import {AnalyzerService} from '@services/analyzer.service';
import {TreeNodeService} from '@services/tree.node.service';
import {ExcelService} from '@services/excel.service';
import {SearchBarComponent} from '@components/analyzer/search-bar/search-bar.component';
import {CalculatorComponent} from '@components/analyzer/calculator/calculator.component';
import {ClassDistributionComponent} from '@components/analyzer/class-distribution/class-distribution.component';
import {TabsComponent} from '@components/analyzer/tabs/tabs.component';
import {TableVisualizationComponent} from '@components/analyzer/tabs/table-visualization/table-visualization.component';

@NgModule({
  declarations: [
    BaseComponent,
    AnalyzerComponent,
    FormComponent,
    VisualizationComponent,
    SearchBarComponent,
    CalculatorComponent,
    ClassDistributionComponent,
    TabsComponent,
    TableVisualizationComponent
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    MAT_MODULES,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    NgxChartsModule
  ],
  providers: [AnalyzerService, TreeNodeService, ExcelService],
  bootstrap: [BaseComponent]
})
export class AppModule { }
