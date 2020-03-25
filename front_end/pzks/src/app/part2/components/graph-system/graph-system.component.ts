import { Component, OnInit } from '@angular/core';
import * as vis from 'vis';
import { DisplayNetworkModel } from '../../models/display/display-network.model';

@Component({
  selector: 'app-graph-system',
  templateUrl: './graph-system.component.html',
  styleUrls: ['./graph-system.component.css']
})
export class GraphSystemComponent implements OnInit {
  graphData: DisplayNetworkModel = this.getDefaultData();

  constructor() { }

  ngOnInit() {
   
  }

  getDefaultData(): DisplayNetworkModel {
    var nodes = new vis.DataSet([
      { id: "1", label: "1 [1]", weight: 1 },
      { id: "2", label: "2 [2]", weight: 2 },
      { id: "3", label: "3 [2]", weight: 2 },
      { id: "4", label: "4 [3]", weight: 3 },
      { id: "5", label: "5 [4]", weight: 4 }
    ]);
  
    // create an array with edges
    var edges = new vis.DataSet([
      { from: "1", to: "3", arrows: "to", label: "[3]", font: { size: 12, color: "red", face: "sans", background: "white" }, weight: 1 },
      { from: "1", to: "2", arrows: "to", label: "[4]", weight: 1 },
      { from: "2", to: "4", arrows: "to", label: "[5]", weight: 1 },
      { from: "2", to: "5", arrows: "to", label: "[6]", weight: 1 },
      { from: "3", to: "3", arrows: "to", label: "[6]", weight: 1 }
    ]);

    var data = new DisplayNetworkModel();
    data.nodes = nodes;
    data.edges = edges;

    return data;
  }

  onGraphChanged(graph: DisplayNetworkModel): void {
    console.log(graph);
    this.graphData = graph;
  }
}
