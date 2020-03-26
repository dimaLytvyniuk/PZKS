import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { DisplayNetworkModel } from '../../models/display/display-network.model';
import { GraphPropsService } from '../../services/graph-props.service';
import { GraphConnectionType } from '../../models/graph-connection-type';
import { NetworkParsingException } from '../../errors/NetworkParsingException';
import { DisplayEdgeModel } from '../../models/display/display-edge.model';
import { DisplayNodeModel } from '../../models/display/display-node.model';
import { StoreNodeModel } from '../../models/store/store-node.model';
import { StoreEdgeModel } from '../../models/store/store-edge.model';
import * as vis from 'vis';
import { StoreNetworkModel } from '../../models/store/store-network-model';
import { DirectedGraphManipulationService } from '../../services/directed-graph-manipulation.service';
import { BaseGraphManipulationService } from '../../services/base-graph-manipulation.service';

@Component({
  selector: 'app-graph-general',
  templateUrl: './graph-general.component.html',
  styleUrls: ['./graph-general.component.css']
})
export class GraphGeneralComponent implements OnInit {
  nodes = null;
  edges = null;
  network = null;

  @Input() data: DisplayNetworkModel;
  @Input() isDirected: boolean = false;
  @Input() isNodesHasWeight: boolean = false;
  @Input() isEdgesHasWeight: boolean = false;
  
  @Output() graphChanged = new EventEmitter<DisplayNetworkModel>();

  graphManipulationService: BaseGraphManipulationService;

  seed = 2;
  
  nodeName = "";
  nodeWeight = "";

  connectionLabel = "";
  cyclicLabel = "";
  
  constructor(
    private graphPropsService: GraphPropsService, 
    private directedGraphManipulationService: DirectedGraphManipulationService
  ) { 
    this.graphManipulationService = directedGraphManipulationService;
  }

  ngOnInit() {
    this.draw();
  }

  destroy() {
    if (this.network !== null) {
      this.network.destroy();
      this.network = null;
    }
  }

  clearPopUp() {
    document.getElementById("saveButton").onclick = null;
    document.getElementById("cancelButton").onclick = null;
    document.getElementById("label-data").style.visibility = "visible";
    document.getElementById("network-popUp").style.display = "none";
    document.getElementById("node-label").setAttribute("value", "");
    document.getElementById("node-weight").setAttribute("value", "");

    this.nodeName = "";
  }
  
  cancelEdit(callback) {
    this.clearPopUp();
    callback(null);
  }
  
  saveNode = (data, callback) => {
    let newNode = this.graphManipulationService.getNewNode(data, this.nodeName, this.nodeWeight, this.data);
  
    this.clearPopUp();
    callback(newNode);
    
    this.onDataChanged();
  }

  saveEdge = (data, callback) => {
    let newEdge = this.graphManipulationService.getNewEdge(data, this.nodeWeight, this.data);

    this.clearPopUp();
    callback(newEdge);

    this.onDataChanged();
  }

  draw() {
    this.destroy();
    this.nodes = [];
    this.edges = [];
  
    // create a network
    var container = document.getElementById("mynetwork");
    var options = {
      layout: { randomSeed: this.seed }, // just to make sure the layout is the same when the locale is changed
      //locale: document.getElementById("locale").nodeValue,
      manipulation: {
        addNode: (data, callback) => this.onAddNode(data, callback),
        editNode: (data, callback) => this.onEditNode(data, callback),
        addEdge: (data, callback) => this.onAddEdge(data, callback),
        deleteNode: (data, callback) => this.onDeleteNode(data, callback),
        deleteEdge: (data, callback) => this.onDeleteEdge(data, callback)
      }
    };

    this.network = new vis.Network(container, this.data, options);
    this.onDataChanged();
  }

  private onAddNode = (data, callback) => {
    // filling in the popup DOM elements
    document.getElementById("operation").innerHTML = "Add Node";
    document.getElementById("network-popUp").style.display = "block";
    document.getElementById("node-label").setAttribute("value", "");
    
    if (this.isNodesHasWeight) {
      document.getElementById("node-weight").setAttribute("value", "");
    } else {
      document.getElementById("node-weight").style.visibility = "hidden";
    }

    data.id = null;
    document.getElementById("saveButton").onclick = () => this.saveNode(data,callback);
    document.getElementById("cancelButton").onclick = () => this.clearPopUp();
  }

  private onEditNode = (data, callback) => {
    if (!this.isNodesHasWeight) {
      callback(data);
      return;
    }

    document.getElementById("operation").innerHTML = "Edit Node";
    document.getElementById("network-popUp").style.display = "block";
    document.getElementById("label-data").style.visibility = "hidden";
    
    this.nodeWeight = data.weight;
    document.getElementById("node-weight").setAttribute("value", data.weight);

    document.getElementById("saveButton").onclick = () => this.saveNode(data,callback);
    document.getElementById("cancelButton").onclick = () => this.cancelEdit(callback);
  }

  private onAddEdge = (data, callback) => {
    if (this.isEdgesHasWeight) {
      document.getElementById("operation").innerHTML = "Add Edge";
      document.getElementById("network-popUp").style.display = "block";
      document.getElementById("label-data").style.visibility = "hidden";
      document.getElementById("node-weight").setAttribute("value", "");
  
      document.getElementById("saveButton").onclick = () => this.saveEdge(data,callback);
      document.getElementById("cancelButton").onclick = () => this.cancelEdit(callback);
    } else {
      this.saveEdge(data, callback);
    }
  }

  private onDeleteNode = (data, callback) => {
    callback(data);
    this.onDataChanged();
  }

  private onDeleteEdge = (data, callback) => {
    callback(data);
    this.onDataChanged();
  }

  onChagedNodeLabelBox($event) {
    this.nodeName = $event.target.value;
    console.log(this.nodeName);
  }

  onChagedNodeWeightBox($event) {
    this.nodeWeight = $event.target.value;
  }

  saveNetwork() {
    let networkModel = this.graphManipulationService.getStoreNetworkModel(this.data);

    localStorage.setItem("network", JSON.stringify(networkModel));
  }

  onSaveToFile() {
    let networkModel = this.graphManipulationService.getStoreNetworkModel(this.data);

    this.graphManipulationService.saveObjectToFile(networkModel, "graph-task.json");
  }

  onFileSelected($event) {
    let files = $event.target.files;
    if (files.length == 0) {
      return;
    }

    let file = files[0];
    let reader = new FileReader();
    reader.addEventListener('load', ($event) => this.onReadedDataFromFile($event));
    reader.readAsText(file);
  }

  onReadedDataFromFile($event) {
    let fileData = $event.target.result;

    this.data = this.parseStringToDisplayNetwork(fileData);
    console.log(this.data);

    this.draw();
  }

  parseStringToDisplayNetwork(fileData: string): DisplayNetworkModel {
    let object = JSON.parse(fileData);
    
    let networkModel = this.graphManipulationService.parseObjectToDisplayNetwork(object);

    return networkModel;
  }

  onDataChanged() {
    this.graphChanged.emit(this.data);
    
    let isCyclicGraph = this.graphPropsService.isCyclicGraph(this.data);
    let connectionType = this.graphPropsService.getGraphConnectionType(this.data);
    
    this.setCyclicLabel(isCyclicGraph);
    this.setConnectionLabel(connectionType);

    this.saveNetwork();
  }

  setConnectionLabel(connectionType: GraphConnectionType): void {
    switch (connectionType) {
      case GraphConnectionType.NotConnected:
        this.connectionLabel = "Граф не зв'язаний";
        break
      case GraphConnectionType.WeaklyConnected:
        this.connectionLabel = "Граф слабозв'язаний";
        break;
      case GraphConnectionType.StronglyConnected:
        this.connectionLabel = "Граф сильнозв'язаний";
        break;
    }
  }

  setCyclicLabel(isCyclicGraph: boolean): void {
    if (isCyclicGraph) {
      this.cyclicLabel = "Граф має циклічну ділянку";
    } else {
      this.cyclicLabel = "Граф ациклічний";
    }
  }
}
