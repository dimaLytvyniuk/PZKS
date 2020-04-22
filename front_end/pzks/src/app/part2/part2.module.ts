import { NgModule, ErrorHandler } from '@angular/core';
import { CommonModule } from '@angular/common';

import { Part2RoutingModule } from './part2-routing.module';
import { MainPageComponent } from './components/main-page/main-page.component';
import { GraphTaskComponent } from './components/graph-task/graph-task.component';
import { GraphSystemComponent } from './components/graph-system/graph-system.component';
import { ModelingComponent } from './components/modeling/modeling.component';
import { StatisticComponent } from './components/statistic/statistic.component';
import { GraphErrorHandler } from './services/graph-error-handler';
import { GraphPropsService } from './services/graph-props.service';
import { GraphGeneralComponent } from './components/graph-general/graph-general.component';
import { DirectedGraphManipulationService } from './services/directed-graph-manipulation.service';
import { UndirectedGraphManipulationService } from './services/undirected-graph-manipulation.service';
import { Lab2Component } from './components/lab2/lab2.component';

@NgModule({
  declarations: [
    MainPageComponent, 
    GraphTaskComponent, 
    GraphSystemComponent, 
    ModelingComponent, 
    StatisticComponent, 
    GraphGeneralComponent,
    Lab2Component
  ],
  imports: [
    CommonModule,
    Part2RoutingModule
  ],
  providers: [
    {
      provide: ErrorHandler,
      useClass: GraphErrorHandler
    },
    GraphPropsService,
    DirectedGraphManipulationService,
    UndirectedGraphManipulationService
  ]
})
export class Part2Module { }
