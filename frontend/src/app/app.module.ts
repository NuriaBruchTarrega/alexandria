import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {MAT_MODULES} from './angular.material';

import {BaseComponent} from './base/base.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

@NgModule({
  declarations: [
    BaseComponent
  ],
  imports: [
    BrowserModule,
    MAT_MODULES,
    BrowserAnimationsModule
  ],
  providers: [],
  bootstrap: [BaseComponent]
})
export class AppModule { }
