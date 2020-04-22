import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import * as vis from 'vis';
import { StoreNetworkModel } from '../../models/store/store-network-model';
import { StoreNodeModel } from '../../models/store/store-node.model';
import { StoreEdgeModel } from '../../models/store/store-edge.model';

@Component({
  selector: 'app-graph-system',
  templateUrl: './graph-system.component.html',
  styleUrls: ['./graph-system.component.css']
})
export class GraphSystemComponent implements OnInit {
  graphData: StoreNetworkModel = this.getDefaultData();

  @Output() graphChanged = new EventEmitter<StoreNetworkModel>();

  constructor() { }

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
  
    // create an array with edges
    var edges = [
       new StoreEdgeModel("1", "3"),
       new StoreEdgeModel("2", "4"),
       new StoreEdgeModel("2", "5"),
       new StoreEdgeModel("3", "3"),
       new StoreEdgeModel("1", "2"),
    ];

    var data = new StoreNetworkModel();
    data.nodes = nodes;
    data.edges = edges;
    data.isDirected = false;
    data.isNodesHasWeight = true;
    data.isEdgesHasWeight = false;
    
    return data;
  }

  onGraphChanged(graph: StoreNetworkModel): void {
    console.log(graph);
    this.graphData = graph;
    this.graphChanged.emit(this.graphData);
  }
}
