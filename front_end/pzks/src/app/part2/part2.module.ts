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
import { OrientedGraphManipulationService } from './services/oriented-graph-manipulation.service';

@NgModule({
  declarations: [
    MainPageComponent, 
    GraphTaskComponent, 
    GraphSystemComponent, 
    ModelingComponent, 
    StatisticComponent, 
    GraphGeneralComponent
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
    OrientedGraphManipulationService
  ]
})
export class Part2Module { }
