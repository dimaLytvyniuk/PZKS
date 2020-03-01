import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { Part2RoutingModule } from './part2-routing.module';
import { MainPageComponent } from './components/main-page/main-page.component';
import { GraphTaskComponent } from './components/graph-task/graph-task.component';
import { GraphSystemComponent } from './components/graph-system/graph-system.component';
import { ModelingComponent } from './components/modeling/modeling.component';
import { StatisticComponent } from './components/statistic/statistic.component';

@NgModule({
  declarations: [MainPageComponent, GraphTaskComponent, GraphSystemComponent, ModelingComponent, StatisticComponent],
  imports: [
    CommonModule,
    Part2RoutingModule
  ]
})
export class Part2Module { }
