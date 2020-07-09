import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppRoutingModule} from './app-routing.module';

import {MAT_MODULES} from './angular.material';

import {BaseComponent} from './base/base.component';
import {AnalyzerComponent} from './analyzer/analyzer.component';
import {FormComponent} from './analyzer/form/form.component';

@NgModule({
  declarations: [
    BaseComponent,
    AnalyzerComponent,
    FormComponent
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    MAT_MODULES
  ],
  providers: [],
  bootstrap: [BaseComponent]
})
export class AppModule { }
