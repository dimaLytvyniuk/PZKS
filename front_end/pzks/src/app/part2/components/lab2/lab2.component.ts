import { Component, OnInit } from '@angular/core';
import { StoreNetworkModel } from '../../models/store/store-network-model';

@Component({
  selector: 'app-lab2',
  templateUrl: './lab2.component.html',
  styleUrls: ['./lab2.component.css']
})
export class Lab2Component implements OnInit {
  graphTaskData: StoreNetworkModel
  graphSystemData: StoreNetworkModel;

  constructor() { }

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
}
