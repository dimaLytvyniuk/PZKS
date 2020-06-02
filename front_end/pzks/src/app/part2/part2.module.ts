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
import { Lab6Component } from './components/lab6/lab6.component';
import { HttpClientModule } from '@angular/common/http';
import { TaskPlannerApiService } from './services/task-planner-api.service';
import { GantDiagramComponent } from './components/gant-diagram/gant-diagram.component';
import { RgrContainerComponent } from './components/rgr-container/rgr-container.component';

@NgModule({
  declarations: [
    MainPageComponent, 
    GraphTaskComponent, 
    GraphSystemComponent, 
    ModelingComponent, 
    StatisticComponent, 
    GraphGeneralComponent,
    Lab6Component,
    GantDiagramComponent,
    RgrContainerComponent
  ],
  imports: [
    CommonModule,
    Part2RoutingModule,
    HttpClientModule
  ],
  providers: [
    {
      provide: ErrorHandler,
      useClass: GraphErrorHandler
    },
    GraphPropsService,
    DirectedGraphManipulationService,
    UndirectedGraphManipulationService,
    TaskPlannerApiService,
  ]
})
export class Part2Module { }
