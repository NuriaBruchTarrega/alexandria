import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AnalyzerComponent} from './components/analyzer/analyzer.component';
import {SensitivityComponent} from '@components/sensitivity/sensitivity.component';


const routes: Routes = [
  {
    path: '',
    redirectTo: '/analyze',
    pathMatch: 'full'
  },
  {
    path: 'analyze',
    component: AnalyzerComponent
  },
  {
    path: 'sensitivity',
    component: SensitivityComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
