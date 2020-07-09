import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AnalyzerComponent} from './components/analyzer/analyzer.component';


const routes: Routes = [
  {
    path: '',
    redirectTo: '/analyze',
    pathMatch: 'full'
  },
  {
    path: 'analyze',
    component: AnalyzerComponent,
    // resolve: { allShoppingListsData: ViewAllListsResolver}
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
