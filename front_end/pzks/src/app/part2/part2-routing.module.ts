import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { MainPageComponent } from './components/main-page/main-page.component';
import { GraphTaskComponent } from './components/graph-task/graph-task.component';
import { GraphSystemComponent } from './components/graph-system/graph-system.component';
import { StatisticComponent } from './components/statistic/statistic.component';
import { ModelingComponent } from './components/modeling/modeling.component';
import { Lab6Component } from './components/lab6/lab6.component';

const routes: Routes = [
  { path: 'part2', component: MainPageComponent},
  { path: 'part2/graph-task', component: GraphTaskComponent},
  { path: 'part2/graph-system', component: GraphSystemComponent},
  { path: 'part2/modeling', component: ModelingComponent },
  { path: 'part2/statistic', component: StatisticComponent },
  { path: 'part2/lab6', component: Lab6Component }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class Part2RoutingModule { }
