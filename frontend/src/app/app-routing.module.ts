import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AnalyzerComponent} from './components/analyzer/analyzer.component';
import {ExperimentsComponent} from '@components/experiments/experiments.component';


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
    component: ExperimentsComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
