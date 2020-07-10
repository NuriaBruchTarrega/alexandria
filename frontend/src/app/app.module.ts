import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppRoutingModule} from './app-routing.module';

import {MAT_MODULES} from './angular.material';

import {BaseComponent} from './components/base/base.component';
import {AnalyzerComponent} from './components/analyzer/analyzer.component';
import {FormComponent} from './components/analyzer/form/form.component';
import {VisualizationComponent} from './components/analyzer/visualization/visualization.component';
import {AnalyzerService} from './services/analyzer.service';

@NgModule({
  declarations: [
    BaseComponent,
    AnalyzerComponent,
    FormComponent,
    VisualizationComponent
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    MAT_MODULES
  ],
  providers: [AnalyzerService],
  bootstrap: [BaseComponent]
})
export class AppModule { }
