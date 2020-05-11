import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import * as vis from 'vis';
import { StoreNetworkModel } from '../../models/store/store-network-model';
import { StoreNodeModel } from '../../models/store/store-node.model';
import { StoreEdgeModel } from '../../models/store/store-edge.model';
import { Observable } from 'rxjs';
import { TaskPlannerApiService } from '../../services/task-planner-api.service';
import { map, mapTo } from "rxjs/operators"

@Component({
  selector: 'app-graph-task',
  templateUrl: './graph-task.component.html',
  styleUrls: ['./graph-task.component.css']
})
export class GraphTaskComponent implements OnInit {
  graphData: StoreNetworkModel = this.getDefaultData();
  lab2Queue$: Observable<any>;
  lab3Queue$: Observable<any>;

  @Output() graphChanged = new EventEmitter<StoreNetworkModel>();

  constructor(private apiService: TaskPlannerApiService) { }

  ngOnInit() {
  }

  getDefaultData(): StoreNetworkModel {
    var nodes = [
      new StoreNodeModel("1", "1 [1]", 1),
      new StoreNodeModel("2", "2 [2]", 2),
      new StoreNodeModel("3", "3 [2]", 2),
      new StoreNodeModel("4", "4 [3]", 3),
      new StoreNodeModel("5", "5 [4]", 4),
    ];
  
    var edges = [
      new StoreEdgeModel("1", "3", 1),
      new StoreEdgeModel("1", "2", 1),
      new StoreEdgeModel("2", "4", 1),
      new StoreEdgeModel("2", "5", 1),
      new StoreEdgeModel("3", "3", 1),
   ];

    var data = new StoreNetworkModel();
    data.nodes = nodes;
    data.edges = edges;
    data.isDirected = true;
    data.isNodesHasWeight = true;
    data.isEdgesHasWeight = true;

    return data;
  }

  onGraphChanged(graph: StoreNetworkModel): void {
    console.log(graph);
    this.graphData = graph;
    this.graphChanged.emit(this.graphData);
  }

  onLab2Click() {
    this.lab2Queue$ = this.apiService.lab2(this.graphData).pipe(
      map(x => x.join(" "))
    )
  }

  onLab3Click() {
    this.lab3Queue$ = this.apiService.lab3(this.graphData).pipe(
      map(x => x.join(" "))
    )
  }
}
