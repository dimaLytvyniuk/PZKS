import { Component, OnInit } from '@angular/core';
import { StoreNetworkModel } from '../../models/store/store-network-model';
import { PlanTasksModel } from '../../models/store/plan-tasks.model';
import { TaskPlannerApiService } from '../../services/task-planner-api.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-lab6',
  templateUrl: './lab6.component.html',
  styleUrls: ['./lab6.component.css']
})
export class Lab6Component implements OnInit {
  graphTaskData: StoreNetworkModel
  graphSystemData: StoreNetworkModel;

  planningResults: Observable<string[][]>;

  constructor(private apiService: TaskPlannerApiService) { }

  ngOnInit() {
  }

  onGraphTaskChanged(graph: StoreNetworkModel): void {
    console.log(graph);
    this.graphTaskData = graph;
  }

  onGraphSystemChanged(graph: StoreNetworkModel): void {
    console.log(graph);
    this.graphSystemData = graph;
  }

  onDoPlan() {
    let planTaskModel = new PlanTasksModel();
    planTaskModel.graphTask = this.graphTaskData;
    planTaskModel.graphSystem = this.graphSystemData;

    this.planningResults = this.apiService.lab6(planTaskModel);
  }
}
