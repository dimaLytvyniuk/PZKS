import { Component, OnInit } from '@angular/core';
import { StoreNetworkModel } from '../../models/store/store-network-model';
import { PlanTasksModel } from '../../models/store/plan-tasks.model';
import { TaskPlannerApiService } from '../../services/task-planner-api.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-lab6',
  templateUrl: './lab6.component.html',
  styleUrls: ['./lab6.component.css']
})
export class Lab6Component implements OnInit {
  graphTaskData: StoreNetworkModel
  graphSystemData: StoreNetworkModel;

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

    this.apiService.lab6(planTaskModel).subscribe(response => {
      console.log(response);
    },
    (err: HttpErrorResponse) => {
      if (err.error instanceof Error) {
        console.log('An error occurred:', err.error.message);
      } else {
        console.log(`Backend returned code ${err.status}, body was: ${err.error}`);
      }
    });
  }
}
